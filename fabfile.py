import fabric.contrib
from fabric.api import cd, env, hide, execute, lcd, settings, local, put, prefix, run, sudo, task 
from fabric.colors import green as _green, yellow as _yellow, red as _red
from fabric.utils import abort
from boto.ec2 import connect_to_region, blockdevicemapping

import fnmatch
import os
import sys
import time
import xml.etree.ElementTree

ENVIRONMENTS = ("dev", "int", "prod", "test")

# connection attributes
env.disable_known_hosts = True
GATEWAY_USER = "deploy"
REMOTE_USER = "ubuntu"
MAX_SSH_RETRIES = 10
env.user = REMOTE_USER

# project attributes
PROJECT_NAME = "royalty-web"
PROJECT_USERS = ("royalty", "royaltyqa")
DEV_DB_NAME = "royalty"
DEV_DB_ROOT_PASSWORD = "root"
DEV_DB_USER = "royalty_bravo"
DEV_DB_QE_USER = "royaltyqa_bravo"
DEV_DB_QE_PASSWORD = "newDbPassword"
DEV_DB_SCHEMA_FILE = "royalty.sql"
DEV_DB_INIT_FILE = "royalty_init.sql"
DB_PASSWORD = "newDbPassword"

VERSION =""
FI001_SFTP_1939_PASSWORD = "newFI001Sftp1939Password"
FI001_SFTP_9077_PASSWORD = "newFI001Sftp9077Password"
CLIENT_SECRET = "newClientSecret"
API_KEYS = "newApiKeys"
MAIL_SERVER_PASSWORD = "newMailServerPassword"

FI022_PASSWORD = "newFI022Password"
FI076_PASSWORD = "newFI076Password"
SFDC_OAUTH2_CLIENT_SECRET = "newSfdcOauth2ClientSecret"
SFDC_OAUTH2_PASSWORD = "newSfdcOAuth2Password"
PAYPAL_OAUTH2_CLIENT_SECRET = "newPayPalOauth2ClientSecret"

# AWS attributes
REGION = "us-east-1"
SECRETS_BUCKET = "maker-deploy"
BASE_SEC_GROUP_ID = "sg-ac5c2ac9"

# EC2 instance attributes
KEY_NAME = "royalty"
INSTANCE_TYPE = "t2.small"
AZ = "us-east-1d"
SUBNET_ID = "subnet-5448973b"
ROOT_VOLUME_SIZE = 16
ROOT_VOLUME_TYPE = "gp2"

# derived
tarGzFile = ""
ver = ""
host_private_ips = []


# inspired by
# http://stackoverflow.com/questions/3654559/how-to-hide-the-password-in-fabric-when-the-command-is-printed-out
class StreamFilter(object):

    def __init__(self, filters, stream):
        self.stream = stream
        self.filters = filters

    def write(self, data):
        for src in self.filters:
            if src:
                data = data.replace(src, '*****')
        self.stream.write(data)
        self.stream.flush()

    def flush(self):
        self.stream.flush()


@task
def deploy(environment="dev", tag="", gateway="", source_image=""):

    env_var_prefix = environment.upper()
    if environment not in ["int", "prod"]:
        env_var_prefix = "INT"

    # get secrets form the environment
    global FI001_SFTP_1939_PASSWORD
    global FI001_SFTP_9077_PASSWORD
    global CLIENT_SECRET
    global API_KEYS
    global FI022_PASSWORD
    global FI076_PASSWORD
    global SFDC_OAUTH2_CLIENT_SECRET
    global SFDC_OAUTH2_PASSWORD
    global PAYPAL_OAUTH2_CLIENT_SECRET
    global DB_PASSWORD

    FI001_SFTP_1939_PASSWORD = os.environ.get(env_var_prefix + '_FI001_SFTP_1939_PASSWORD')
    FI001_SFTP_9077_PASSWORD = os.environ.get(env_var_prefix + '_FI001_SFTP_9077_PASSWORD')
    CLIENT_SECRET = os.environ.get(env_var_prefix + '_OAUTH2_CLIENT_SECRET')
    API_KEYS = os.environ.get(env_var_prefix + '_API_KEYS')
    FI022_PASSWORD = os.environ.get(env_var_prefix + '_FI022_SFTP_PASSWORD')
    FI076_PASSWORD = os.environ.get(env_var_prefix + '_FI076_SFTP_PASSWORD')
    SFDC_OAUTH2_CLIENT_SECRET = os.environ.get(env_var_prefix + '_SFDC_OAUTH2_CLIENT_SECRET')
    SFDC_OAUTH2_PASSWORD = os.environ.get(env_var_prefix + '_SFDC_OAUTH2_PASSWORD')
    PAYPAL_OAUTH2_CLIENT_SECRET = os.environ.get(env_var_prefix + '_PAYPAL_OAUTH2_CLIENT_SECRET')
    DB_PASSWORD = os.environ.get(env_var_prefix + '_DB_PASSWORD')

    _findTarGzFile()

    if gateway:
        env.gateway = "%s@%s" % (GATEWAY_USER, gateway)     # e.g. deploy@54.208.125.94
        env.forward_agent = True

    # construct instance name tag
    feature_tag = "-".join(tag.split("-")[1:]).upper()
    instance_name_tag = "%s/%s" % (environment, PROJECT_NAME)   # e.g. dev/royalty-web_ROY-1
    if environment == "dev":
        instance_name_tag += "_%s" % feature_tag

    # look up running instance(s) matching name tag prefix
    conn = _create_connection(REGION)
    instances = _find_instances(conn, instance_name_tag)
    global host_private_ips
    host_private_ips = _get_private_ips(instances)

    # if dev, provision new instance/dependencies if not already present
    if environment == "dev":
        execute(dev, environment, conn, instances, instance_name_tag, source_image)

    # deploy application
    if environment in ENVIRONMENTS:
        execute(deploy_app, environment=environment, hosts=host_private_ips)
    else:
        abort(_red("Unsupported environment: %s") % environment)


@task
def dev(environment, conn, instances, instance_name_tag, source_image):
    terraform_apply(environment)
    if not instances:
        host_private_ip = _launch_instance(conn, instance_name_tag, source_image)

        global host_private_ips
        host_private_ips = [host_private_ip]

        with settings(host_string=REMOTE_USER + "@" + host_private_ip):
            # provision app user accounts
            for user in PROJECT_USERS:
                _create_user(user)

            # add qa ssh key (optional)
            qa_pub_key = os.environ.get('QA_PUB_KEY')
            if qa_pub_key:
                _add_key(REMOTE_USER, qa_pub_key)

            _setup_app()


def _setup_app():
    global DEV_DB_QE_PASSWORD
    global DB_PASSWORD
    DEV_DB_QE_PASSWORD = os.environ.get('DB_QE_PASSWORD')
    DB_PASSWORD = os.environ.get('DB_PASSWORD')

    print(_yellow("Setting up app directories"))
    _add_dirs(["/data", "~/target"])
    _set_owner("/data", PROJECT_USERS[0])

    java_home_path = run("echo $JAVA_HOME")
    _add_symlink(java_home_path, "/data/java")

    # setup mysql and import schema
    print(_yellow("Creating database, loading schema, creating users"))
    _run_mysql_query("CREATE DATABASE %s" % DEV_DB_NAME)

    # import DB schema
    _run_mysql_import(DEV_DB_SCHEMA_FILE)

    # import DB init data
    _run_mysql_import(DEV_DB_INIT_FILE)

    # obfuscate passwords on stdout
    sys.stdout = StreamFilter([DB_PASSWORD, DEV_DB_QE_PASSWORD], sys.stdout)

    # add DB users
    _run_mysql_query("GRANT ALL PRIVILEGES ON %s.* TO '%s'@'%%' IDENTIFIED BY '%s'" %
                     (DEV_DB_NAME, DEV_DB_USER, DB_PASSWORD))
    _run_mysql_query("GRANT SELECT ON %s.* TO '%s'@'%%' IDENTIFIED BY '%s'" %
                     (DEV_DB_NAME, DEV_DB_QE_USER, DEV_DB_QE_PASSWORD))
    _run_mysql_query("FLUSH PRIVILEGES")

    # configure nginx
    print(_yellow("Configuring NGINX"))
    with cd("/etc/nginx"):
        sudo("rm sites-available/*")
        sudo("rm sites-enabled/*")
        put("conf/%s-nginx.conf" % PROJECT_NAME, "sites-available/%s" % PROJECT_NAME, use_sudo=True)
        _set_owner("sites-available/*", "root")
        _add_symlink("sites-available/%s" % PROJECT_NAME, "sites-enabled/%s" % PROJECT_NAME)

    sudo("nginx -t")


@task
def deploy_app(environment):
    put_and_untar_artifact(environment)
    link_artifact()
    restart_server()


def _findTarGzFile():
    global tarGzFile
    global ver
    files = _find("%s-*.tar.gz" % PROJECT_NAME, "target")
    if len(files) == 0:
        raise TypeError("%s-*.tar.gz not found" % PROJECT_NAME)
    tarGzFile = files[0]
    print ".tar.gz file = %s" % tarGzFile
    ver = tarGzFile[:-len(".tar.gz")]
    print "ver = %s" % ver


# find files by pattern
def _find(pattern, path):
    result = []
    for root, dirs, files in os.walk(path):
        for name in files:
            if fnmatch.fnmatch(name, pattern):
                # result.append(os.path.join(root, name))
                # only interested in name
                result.append(name)
    return result


def terraform_apply(environment):
    print(_yellow("Invoking Terraform to provision or verify infrastructure"))
    with lcd("scripts/terraform"):
        local("terraform remote config -backend=S3 -backend-config=\"bucket=" + SECRETS_BUCKET +
              "\" -backend-config=\"key=" + PROJECT_NAME + "/" + environment +
              "/terraform.tfstate\" -backend-config=\"region=" + REGION + "\"")
        local("terraform apply")


# Private method for getting AWS connection
def _create_connection(region):
    print(_yellow("\nConnecting to %s") % region)

    connection = connect_to_region(region_name=region)

    print(_green("Connection to AWS established"))
    return connection


def _get_private_ips(instances):
    ips = []
    for inst in instances:
        ips.append(inst.private_ip_address)

    return ips


def _get_tf_output_var(tf_output_var):
    with lcd("scripts/terraform"):
        tf_output_value = local("terraform output " + tf_output_var, capture=True)

    return tf_output_value


# check ec2 for running instance(s) with name matching feature branch
def _find_instances(conn, tag):
    print(_yellow("\nChecking for running instance(s) with Name tag: %s") % tag)
    reservations = conn.get_all_reservations(filters={'tag:Name': tag + "*"})
    running_instances = []

    for res in reservations:
        for inst in res.instances:
            if inst.state == "running":
                running_instances.append(inst)

    if not running_instances:
        print(_yellow("\nNo matching or running instance(s) found"))
    else:
        print("\nFound matching running instances (%d):" % len(running_instances))
        for inst in running_instances:
            print "ID: %s, IP: %s" % (inst.id, inst.private_ip_address)

    return running_instances


def _launch_instance(conn, tag, source_image):
    print(_yellow("\nLaunching new instance..."))

    app_sec_group_id = _get_tf_output_var("security_group_id")
    print("App security group id: %s" % app_sec_group_id)

    iam_profile = _get_tf_output_var("iam_instance_profile_name")
    print("IAM instance profile name: %s" % iam_profile)

    # define root volume
    dev_sda1 = blockdevicemapping.EBSBlockDeviceType()
    dev_sda1.size = ROOT_VOLUME_SIZE
    dev_sda1.volume_type = ROOT_VOLUME_TYPE
    dev_sda1.delete_on_termination = True
    bdm = blockdevicemapping.BlockDeviceMapping()
    bdm['/dev/sda1'] = dev_sda1

    reservation = conn.run_instances(
        image_id=source_image,
        key_name=KEY_NAME,
        instance_type=INSTANCE_TYPE,
        placement=AZ,
        subnet_id=SUBNET_ID,
        instance_initiated_shutdown_behavior="stop",
        security_group_ids=[BASE_SEC_GROUP_ID, app_sec_group_id],
        instance_profile_name=iam_profile,
        block_device_map=bdm,
        dry_run=False)

    instance = reservation.instances[0]

    print("Instance ID: %s" % instance.id)
    print("Instance IP: %s" % instance.private_ip_address)

    # tag new instance
    instance.add_tag("Name", tag)

    time.sleep(10)
    _confirm_instance_access(instance)

    return instance.private_ip_address


def _confirm_instance_access(instance):
    print(_yellow("Waiting for running instance"))

    hostname = None
    status = instance.update()
    while status == "pending":
        time.sleep(10)
        print(_yellow("Instance status: %s") % status)
        status = instance.update()

    if status == "running":
        print(_green("Instance status: %s" % status))
        print(_yellow("Waiting to confirm SSH access"))
        retry = 1
        while True:
            try:
                with settings(user=REMOTE_USER, host_string=instance.private_ip_address,
                              command_timeout=10, abort_on_prompts=True):
                    hostname = run("hostname", quiet=True)
                    print(_green("Instance accessible, hostname: %s") % hostname)
                    break
            except:
                if retry == MAX_SSH_RETRIES:
                    abort(_red("Failed to connect to %s via SSH after %s attempts") %
                          (instance.private_ip_address, str(retry)))

                print(_yellow("SSH connection attempt %s to %s has failed, retrying...") %
                      (str(retry), instance.private_ip_address))
                retry += 1
                time.sleep(60)
    else:
        abort(_red("Instance failed to start, status: %s") % status)

    return hostname


def _create_user(name):
    print(_yellow("Adding user: %s") % name)

    cmd = "sudo useradd -d /home/%s -m %s -s /bin/bash" % (name, name)
    sudo(cmd, user=REMOTE_USER)


def _add_key(user, pub_key):
    print(_yellow("Adding public SSH key to user: %s") % user)

    user_home = run("echo ~%s" % user)
    auth_file = "%s/.ssh/authorized_keys" % user_home
    fabric.contrib.files.append(filename=auth_file, text=pub_key, use_sudo=True)


def _add_dirs(dirs):
    for directory in dirs:
        cmd = "sudo mkdir -p %s" % directory
        sudo(cmd, user=REMOTE_USER)


def _set_owner(directory, user):
    cmd = "sudo chown %s:%s %s" % (user, user, directory)
    sudo(cmd, user=REMOTE_USER)


def _add_symlink(target, link_name):
    sudo("ln -srf %s %s" % (target, link_name))


def _run_mysql_query(query):
    with settings(prompts={'Enter password: ': DEV_DB_ROOT_PASSWORD}):
        run("mysql -u root -p -e \"%s\"" % query)


def _run_mysql_import(sql_file):
    put("src/main/sql/%s" % sql_file, "/tmp/%s" % sql_file)
    with settings(prompts={'Enter password: ': DEV_DB_ROOT_PASSWORD}):
        run("mysql -u root -p %s < /tmp/%s" % (DEV_DB_NAME, sql_file))
    run("rm /tmp/%s" % sql_file)

# ---------------------------------------------------------------


@task
def put_and_untar_artifact(environment):
    global MAIL_SERVER_PASSWORD
    MAIL_SERVER_PASSWORD = os.environ.get('MAIL_SERVER_PASSWORD')
    with cd("/data"):
        # put and untar
        localTarGzFile = "./target/%s" % tarGzFile
        #print localTarGzFile
        # rm ver if it has been deployed before
        with settings(warn_only=True):
            sudo("rm -rf %s*" % ver, user=PROJECT_USERS[0])
        put("%s" % localTarGzFile, "./%s" % tarGzFile, use_sudo=True)
        run('ls -lF')
        sudo("tar -zxvf %s" % tarGzFile, user=PROJECT_USERS[0])
        run("ls -lF")
        # use sudo() to replace remote properties

        # enable backdoor on dev to bypass OAuth
        if environment == "dev":
            sedCmd = "sed -i.bak 's/royalty-web\.oauth2\.authenticationEnabled=true/royalty-web\.oauth2\.authenticationEnabled=false/g' " \
                     "%s/conf/royalty-web.properties" % ver
            sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/dummyFI001Sftp1939Password/%s/g' %s/conf/royalty-web-override.properties" \
            % (FI001_SFTP_1939_PASSWORD, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/dummyFI001Sftp9077Password/%s/g' %s/conf/royalty-web-override.properties" \
            % (FI001_SFTP_9077_PASSWORD, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/dummyClientSecret/%s/g' %s/conf/royalty-web-override.properties" \
            % (CLIENT_SECRET, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/dummyApiKeys/%s/g' %s/conf/royalty-web-override.properties" \
            % (API_KEYS, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/dummyMailServerPassword/%s/g' %s/conf/royalty-web-override.properties" \
            % (MAIL_SERVER_PASSWORD, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/dummyDbPassword/%s/g' %s/conf/royalty-web-override.properties" \
            % (DB_PASSWORD, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/dummyFI022Password/%s/g' %s/conf/royalty-web-override.properties" \
            % (FI022_PASSWORD, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/sdfcOauth2ClientSecret/%s/g' %s/conf/royalty-web-override.properties" \
            % (SFDC_OAUTH2_CLIENT_SECRET, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/sfdcOauth2Password/%s/g' %s/conf/royalty-web-override.properties" \
            % (SFDC_OAUTH2_PASSWORD, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/paypalOauth2ClientSecret/%s/g' %s/conf/royalty-web-override.properties" \
            % (PAYPAL_OAUTH2_CLIENT_SECRET, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])
        sedCmd = "sed -i.bak 's/dummyFI076Password/%s/g' %s/conf/royalty-web-override.properties" \
            % (FI076_PASSWORD, ver)
        sudo(sedCmd, user=PROJECT_USERS[0])


@task
def link_artifact():
    with cd("/data"):
        # link latest to royalty-web
        with settings(warn_only=True):
            sudo('rm %s' % PROJECT_NAME, user=PROJECT_USERS[0])
            sudo("ln -s %s %s" % (ver, PROJECT_NAME), user=PROJECT_USERS[0])

        # Delete all but the most recent release directories and .tar.gz
        # http://stackoverflow.com/a/10119963/349427
        # delete all but the 3 most recent .tar.gz
        sudo("ls -tr %s-*.tar.gz | grep %s | head -n -3 | xargs --no-run-if-empty rm -r" %
             (PROJECT_NAME, PROJECT_NAME), user=PROJECT_USERS[0])
        # delete all but the 3 most recent royalty-web-* directory.  -d option lists directories
        sudo("ls -trd %s-* | grep %s | head -n -3 | xargs --no-run-if-empty rm -r" %
             (PROJECT_NAME, PROJECT_NAME), user=PROJECT_USERS[0])


@task
def restart_server():
    with cd("/data/%s" % PROJECT_NAME):
        with settings(warn_only=True):
            with prefix('export JAVA_HOME=/data/java'):
                # run("ls -lF")
                # run('env')
                sudo("./bin/stopRoyaltyWeb.sh", user=PROJECT_USERS[0])
                sudo("nohup ./bin/startRoyaltyWeb.sh", user=PROJECT_USERS[0])

    sudo("nginx -s reload")


@task
def host_type():
    run('uname -a')

# ---------------------------------------------------------------


# fab build_and_debug:1.0.0-SNAPSHOT-local
# fab build_and_debug:version=1.0.0-SNAPSHOT-local,password=password
@task
def build_and_debug():
    with settings(warn_only=True):
        read_build_version()
        local("pwd")
        local("./target/in-auth-%s/bin/stopRoyaltyWeb.sh" % VERSION)
        local("mvn clean install -P local")
        local("cd target; tar -zxvf in-auth-%s.tar.gz" % VERSION)
        replace_properties()
        #
        local("cd target/in-auth-%s; ./bin/debugInAuthWeb.sh" % VERSION)
        print "\nwait 3 seconds before tailing\n"
        time.sleep(3)
        local("tail -100f target/in-auth-%s/logs/*.stderrout.log" % VERSION)


@task
def build_skip_tests_and_debug():
    with settings(warn_only=True):
        read_build_version()
        local("pwd")
        local("./target/royalty-web-%s/bin/stopRoyaltyWeb.sh" % VERSION)
        local("mvn clean install -DskipTests -P local")
        local("cd target; tar -zxvf royalty-web-%s.tar.gz" % VERSION)
        replace_properties()
        #
        local("cd target/royalty-web-%s; ./bin/debugRoyaltyWeb.sh" % VERSION)
        print "\nwait 3 seconds before tailing\n"
        time.sleep(3)
        local("tail -100f target/royalty-web-%s/logs/*.stderrout.log" % VERSION)


@task
def build_and_start():
    with settings(warn_only=True):
        read_build_version()
        local("pwd")
        local("./target/royalty-web-%s/bin/stopRoyaltyWeb.sh" % VERSION)
        local("mvn clean install -P local")
        local("cd target; tar -zxvf royalty-web-%s.tar.gz" % VERSION)
        replace_properties()
        #
        local("cd target/royalty-web-%s; ./bin/startRoyaltyWeb.sh" % VERSION)
        print "\nwait 3 seconds before tailing\n"
        time.sleep(3)
        local("tail -100f target/royalty-web-%s/logs/*.stderrout.log" % VERSION)


@task
def restart_and_debug():
    with settings(warn_only=True):
        read_build_version()
        local("pwd")
        local("./target/royalty-web-%s/bin/stopRoyaltyWeb.sh" % VERSION)
        print "\nwait 3 seconds before starting in debug mode\n"
        time.sleep(3)
        local("cd target/royalty-web-%s; ./bin/debugRoyaltyWeb.sh" % VERSION)
        print "\nwait 3 seconds before tailing\n"
        time.sleep(3)
        local("tail -100f target/royalty-web-%s/logs/*.stderrout.log" % VERSION)


def replace_properties():
    global FI001_SFTP_1939_PASSWORD
    global FI001_SFTP_9077_PASSWORD
    global CLIENT_SECRET
    global API_KEYS
    global MAIL_SERVER_PASSWORD
    global DB_PASSWORD
    global FI022_PASSWORD
    global FI076_PASSWORD
    global SFDC_OAUTH2_CLIENT_SECRET
    global SFDC_OAUTH2_PASSWORD
    global PAYPAL_OAUTH2_CLIENT_SECRET
    read_build_version()
    # set password or secret from environment variables in file with sed
    FI001_SFTP_1939_PASSWORD = os.environ.get('INT_FI001_SFTP_1939_PASSWORD')
    FI001_SFTP_9077_PASSWORD = os.environ.get('INT_FI001_SFTP_9077_PASSWORD')
    CLIENT_SECRET = os.environ.get('DEV_OAUTH2_CLIENT_SECRET') 
    API_KEYS = os.environ.get('DEV_API_KEYS') 
    MAIL_SERVER_PASSWORD = os.environ.get('MAIL_SERVER_PASSWORD') 
    DB_PASSWORD = os.environ.get('DB_PASSWORD')
    FI022_PASSWORD = os.environ.get('INT_FI022_SFTP_PASSWORD')
    FI076_PASSWORD = os.environ.get('INT_FI076_SFTP_PASSWORD')
    SFDC_OAUTH2_CLIENT_SECRET = os.environ.get('INT_SFDC_OAUTH2_CLIENT_SECRET')
    SFDC_OAUTH2_PASSWORD = os.environ.get('INT_SFDC_OAUTH2_PASSWORD')
    PAYPAL_OAUTH2_CLIENT_SECRET = os.environ.get('INT_PAYPAL_OAUTH2_CLIENT_SECRET')
    sedCmd = "sed -i.bak 's/dummyFI001Sftp1939Password/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (FI001_SFTP_1939_PASSWORD, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/dummyFI001Sftp9077Password/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (FI001_SFTP_9077_PASSWORD, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/dummyClientSecret/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (CLIENT_SECRET, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/dummyApiKeys/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (API_KEYS, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/dummyMailServerPassword/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (MAIL_SERVER_PASSWORD, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/dummyDbPassword/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (DB_PASSWORD, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/dummyFI022Password/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (FI022_PASSWORD, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/dummyFI076Password/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (FI076_PASSWORD, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/sdfcOauth2ClientSecret/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (SFDC_OAUTH2_CLIENT_SECRET, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/sfdcOauth2Password/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (SFDC_OAUTH2_PASSWORD, VERSION)
    local(sedCmd)
    sedCmd = "sed -i.bak 's/paypalOauth2ClientSecret/%s/g' target/royalty-web-%s/conf/royalty-web-override.properties" \
        % (PAYPAL_OAUTH2_CLIENT_SECRET, VERSION)
    local(sedCmd)

@task
def push_and_tag():
    with settings(warn_only=True), hide('stderr'):
        # empty commit and then push
        result = local("git commit --allow-empty -m 'push_and_tag'", capture=True)
        result = local("git push", capture=True)
        # find branch and set qeTag
        branch = local("git rev-parse --abbrev-ref HEAD", capture=True)
        if branch == "develop":
            qeTag = "QE-ROY-0"
        else:
            startPos = branch.find("ROY")
            qeTag = "QE-" + branch[startPos:]
        print "qeTag=%s" % qeTag
        # delete remote qeTag, tag and then push
        result = local("git ls-remote --tags origin | grep %s" % qeTag, capture=True)
        if (result != ""):
            result = local("git push --delete origin %s" % qeTag)
        result = local("git tag -d %s" % qeTag)
        local("git tag %s" % qeTag)
        local("git push origin tag %s" % qeTag)
        
@task        
def read_build_version():
    global VERSION
    root=xml.etree.ElementTree.parse("pom.xml").getroot()
    VERSION=root.find("{http://maven.apache.org/POM/4.0.0}version").text + "-local"
    print("VERSION=%s" % VERSION)

