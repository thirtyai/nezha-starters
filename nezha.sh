#!/bin/sh
# README.md
# -d do function
# -v version
# -p params
# ./nezha.sh -d [changeNezha/changeSpring] -v []  -p []
#
cygwin=false
darwin=false
os400=false
case "$(uname)" in
CYGWIN*) cygwin=true ;;
Darwin*) darwin=true ;;
OS400*) os400=true ;;
esac

error_exit() {
    echo "ERROR: $1 !!"
    exit 1
}

export BASE_DIR=$(
    cd $(dirname $0)/..
    pwd
)

export NEZHA_ROOT=$(pwd)
export NEZHA_CORE_DIR=$BASE_DIR/nezha-starters/nezha-core-starter
export NEZHA_CORE_CORE_JAVA=$NEZHA_CORE_DIR/src/main/java/com/thirtyai/nezha/core/
export NEZHA_BOM_DIR=$NEZHA_ROOT/nezha-bom-starter
export MODE="dev"
export DO_FUNCTION="showinfo"
export OLDER_VERSION=""
export PARAMERS=""
export NEW_VERSION=""

while getopts :d:v:p: opt; do
    case $opt in
    d)
        DO_FUNCTION=$OPTARG
        ;;
    v)
        NEW_VERSION=$OPTARG
        ;;
    p)
        PARAMERS=$OPTARG
        ;;
    ?)
        echo "Unknown parameter $opt Value is $OPTARG"
        exit 1
        ;;
    esac
done

changeNezhaVersion() {
    if $drawin; then
        cd $NEZHA_ROOT
        ./mvnw versions:set -DnewVersion=$1
        cd $NEZHA_BOM_DIR
        sed -i "" "s/\<nezha\.starters\.version\>.*\<\/nezha\.starters\.version\>/\<nezha\.starters\.version\>$1\<\/nezha\.starters\.version\>/g" pom.xml
        sed -i "" "s/\<version\>.*\<\/version\>\<\!\-\- 主版本 \-\-\>/\<version\>$1\<\/version\>\<\!\-\- 主版本 \-\-\>/g" pom.xml
        cd $NEZHA_CORE_CORE_JAVA
        sed -i "" "s/public final static String VERSION = \".*\";/public final static String VERSION = \"$1\";/g" Nezha.java
        cd $NEZHA_ROOT
        ./mvnw clean install -Dmaven.test.skip=true -P release-prod
        ./mvnw versions:commit
    elif $cgwin; then
        cd $NEZHA_ROOT
        ./mvnw versions:set -DnewVersion=$1
        cd $NEZHA_BOM_DIR
        sed -i "s/\<nezha\.starters\.version\>.*\<\/nezha\.starters\.version\>/\<nezha\.starters\.version\>$1\<\/nezha\.starters\.version\>/g" pom.xml
        sed -i "s/\<version\>.*\<\/version\>\<\!\-\- 主版本 \-\-\>/\<version\>$1\<\/version\>\<\!\-\- 主版本 \-\-\>/g" pom.xml
        cd $NEZHA_CORE_CORE_JAVA
        sed -i "s/public final static String VERSION = \".*\";/public final static String VERSION = \"$1\";/g" Nezha.java
        cd $NEZHA_ROOT
        ./mvnw clear install -Dmaven.test.skip=true -P release-prod
        ./mvnw versions:commit
    fi
    echo 'mvn changeVersion success!'
    exit 1
}

changeSpringBootVersion() {
    if $drawin; then
        cd $NEZHA_ROOT
        sed -i "" "s/\<version\>.*\<\/version\>\<\!\-\-spring boot version\-\-\>/\<version\>$1\<\/version\>\<\!\-\-spring boot version\-\-\>/g" pom.xml
        cd $NEZHA_BOM_DIR
        sed -i "" "s/\<version\>.*\<\/version\>\<\!\-\-spring boot version\-\-\>/\<version\>$1\<\/version\>\<\!\-\-spring boot version\-\-\>/g" pom.xml
        sed -i "" "s/\<spring\.boot\.version\>.*\<\/spring\.boot\.version\>\<\!\-\-spring boot version\-\-\>/\<spring\.boot\.version\>$1\<\/spring\.boot\.version\>\<\!\-\-spring boot version\-\-\>/g" pom.xml
    elif $cgwin; then
        cd $NEZHA_ROOT
        sed -i "s/\<version\>.*\<\/version\>\<\!\-\-spring boot version\-\-\>/\<version\>$1\<\/version\>\<\!\-\-spring boot version\-\-\>/g" pom.xml
        cd $NEZHA_BOM_DIR
        sed -i "s/\<version\>.*\<\/version\>\<\!\-\-spring boot version\-\-\>/\<version\>$1\<\/version\>\<\!\-\-spring boot version\-\-\>/g" pom.xml
        sed -i "s/\<spring\.boot\.version\>.*\<\/spring\.boot\.version\>\<\!\-\-spring boot version\-\-\>/\<spring\.boot\.version\>$1\<\/spring\.boot\.version\>\<\!\-\-spring boot version\-\-\>/g" pom.xml
    fi
    echo 'mvn changeSpringBootVersion success!'
    exit 1
}

if [[ "$DO_FUNCTION" == "changeNezha" ]]; then
    changeNezhaVersion $NEW_VERSION
elif [[ "$DO_FUNCTION" == "changeSpring" ]]; then
    changeSpringBootVersion $NEW_VERSION
else
    echo 'no -d option '
fi
