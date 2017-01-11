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

VERSION="0.0.1"

@task
def build_and_debug(version=VERSION):
    with settings(warn_only=True):
        local("pwd")
        #local("curl -X POST -u steve:kuo localhost:8090/coding/shutdown") # not necessary, CTRL-C works fine
        local("./gradlew clean build")
        local("java -server -Xms1700M -Xmx1700M -Xdebug -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n -jar build/libs/coding-%s.jar" % version)
   
@task
def build_skip_tests_and_debug(version=VERSION):
    with settings(warn_only=True):
        print "\nTODO\n"

@task
def build_and_start(version=VERSION):
    with settings(warn_only=True):
        local("pwd")
        #local("curl -X POST -u steve:kuo localhost:8090/coding/shutdown") # not necessary, CTRL-C works fine
        local("./gradlew clean build")
        local("java -server -Xms1700M -Xmx1700M -jar build/libs/coding-%s.jar" % version)

@task
def restart_and_debug(version=VERSION):
    with settings(warn_only=True):
        local("pwd")
        #local("curl -X POST -u steve:kuo localhost:8090/coding/shutdown") # not necessary, CTRL-C works fine
        local("java -server -Xms1700M -Xmx1700M -Xdebug -Xrunjdwp:transport=dt_socket,address=4000,server=y,suspend=n -jar build/libs/coding-%s.jar" % version)

@task
def build_and_startDocker(version=VERSION):
    with settings(warn_only=True):
        local("pwd")
        local("./gradlew clean build")
        local("./gradlew buildDocker")
        startDocker()
        
@task
def startDocker(version=VERSION):
    with settings(warn_only=True):
        local("pwd")
        local("docker run -p:8080:8080 -v /data:/data -t --rm coding:%s --spring.profiles.active=dev" % version)
    

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
