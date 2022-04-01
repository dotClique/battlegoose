# syntax=docker/dockerfile:1.4
FROM openjdk:11-jdk

WORKDIR ci
ENV ANDROID_COMPILE_SDK="32" ANDROID_BUILD_TOOLS="32.0.0" ANDROID_SDK_TOOLS="6514223" \
ANDROID_HOME="${PWD}/android-home"
RUN <<EOF
    apt-get --quiet update --yes
    apt-get --quiet install --yes wget tar unzip lib32stdc++6 lib32z1

    # Create a new directory at specified location
    install -d $ANDROID_HOME
    # Here we are installing androidSDK tools from official source,
    # (the key thing here is the url from where you are downloading these sdk tool for command line, so please do note this url pattern there and here as well)
    # after that unzipping those tools and
    # then running a series of SDK manager commands to install necessary android SDK packages that'll allow the app to build
    wget --output-document=$ANDROID_HOME/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip
    # move to the archive at ANDROID_HOME
    cd $ANDROID_HOME
    unzip -d cmdline-tools cmdline-tools.zip
    cd -
EOF

ENV ANDROID_SDK_ROOT="$ANDROID_HOME" PATH=$PATH:${ANDROID_HOME}/cmdline-tools/tools/bin/
