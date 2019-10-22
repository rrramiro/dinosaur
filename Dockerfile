# Start from Alpine and Java 8, and name this stage "build"
FROM openjdk:8u222-slim AS build
# Install C libraries and build tools
RUN echo "installing dependencies" \
    && apt-get update \
    && apt-get install -y build-essential clang musl-dev libgc-dev libc-dev libunwind-dev libuv1-dev wget git libre2-dev 
# Install re2 from source for clang++ compatability
#RUN git clone https://github.com/google/re2.git && cd re2 && CXX=clang++ make && make install

# Install SBT
ENV SBT_VERSION 1.2.6
ENV SBT_HOME /usr/local/sbt
ENV PATH ${PATH}:${SBT_HOME}/bin
RUN echo "installing SBT $SBT_VERSION" \
    && mkdir -p "$SBT_HOME" \
    && wget -qO - --no-check-certificate "https://piccolo.link/sbt-$SBT_VERSION.tgz" | tar xz -C $SBT_HOME --strip-components=1 \
    && echo -ne "- with sbt $SBT_VERSION\n" >> /root/.built \
    && sbt sbtVersion

# Set up the directory structure for our project
RUN mkdir -p /root/project-build/project
WORKDIR /root/project-build

# Resolve all our dependencies and plugins to speed up future compilations
ADD ./project/plugins.sbt /root/project-build/project
ADD ./project/build.properties /root/project-build/project
ADD ./dinosaur/build.sbt /root/project-build/
RUN sbt update

# Add and compile our actual application source code
ADD ./.scalafmt.conf /root/project-build/.scalafmt.conf
ADD ./dinosaur/src /root/project-build/src
RUN sbt clean nativeLink

# Copy the binary executable to a consistent location
RUN cp ./target/scala-2.11/*-out ./dinosaur-build-out

#Start over from a clean image
FROM httpd:2.4

# Copy in C libraries
COPY --from=build \
  /usr/lib/x86_64-linux-gnu/libre2.so.5 \
  /usr/lib/x86_64-linux-gnu/libuv.so.1 \
  /usr/lib/x86_64-linux-gnu/libunwind.so.8 \
  /usr/lib/x86_64-linux-gnu/libunwind-x86_64.so.8 \
  /usr/lib/x86_64-linux-gnu/libstdc++.so.6 \
  /usr/lib/x86_64-linux-gnu/libgc.so.1 \
  /usr/lib/x86_64-linux-gnu/

# Copy in the executable
COPY --from=build \
  /root/project-build/dinosaur-build-out /usr/local/apache2/cgi-bin/app

COPY httpd_new.conf /usr/local/apache2/conf/httpd.conf
