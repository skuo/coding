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
        local("./target/coding-%s/bin/stopCoding.sh" % VERSION)
        local("mvn clean install -P local")
        local("cd target; tar -zxvf coding-%s.tar.gz" % VERSION)
        #
        local("cd target/coding-%s; ./bin/debugCoding.sh" % VERSION)
        print "\nwait 3 seconds before tailing\n"
        time.sleep(3)
        local("tail -100f target/coding-%s/logs/*.stderrout.log" % VERSION)


@task
def build_skip_tests_and_debug():
    with settings(warn_only=True):
        read_build_version()
        local("pwd")
        local("./target/coding-%s/bin/stopCoding.sh" % VERSION)
        local("mvn clean install -DskipTests -P local")
        local("cd target; tar -zxvf coding-%s.tar.gz" % VERSION)
        replace_properties()
        #
        local("cd target/coding-%s; ./bin/debugCoding.sh" % VERSION)
        print "\nwait 3 seconds before tailing\n"
        time.sleep(3)
        local("tail -100f target/coding-%s/logs/*.stderrout.log" % VERSION)


@task
def build_and_start():
    with settings(warn_only=True):
        read_build_version()
        local("pwd")
        local("./target/coding-%s/bin/stopCoding.sh" % VERSION)
        local("mvn clean install -P local")
        local("cd target; tar -zxvf coding-%s.tar.gz" % VERSION)
        replace_properties()
        #
        local("cd target/coding-%s; ./bin/startCoding.sh" % VERSION)
        print "\nwait 3 seconds before tailing\n"
        time.sleep(3)
        local("tail -100f target/coding-%s/logs/*.stderrout.log" % VERSION)


@task
def restart_and_debug():
    with settings(warn_only=True):
        read_build_version()
        local("pwd")
        local("./target/coding-%s/bin/stopCoding.sh" % VERSION)
        print "\nwait 3 seconds before starting in debug mode\n"
        time.sleep(3)
        local("cd target/coding-%s; ./bin/debugCoding.sh" % VERSION)
        print "\nwait 3 seconds before tailing\n"
        time.sleep(3)
        local("tail -100f target/coding-%s/logs/*.stderrout.log" % VERSION)


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

