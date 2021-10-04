FROM maven:3.8.1-openjdk-11 AS build
RUN mkdir -p /workspace
WORKDIR /workspace
COPY pom.xml /workspace
COPY src /workspace/src
RUN mvn -B -f pom.xml clean package -DskipTests -Dhttps.protocols=TLSv1.2

FROM openjdk:11.0.12-jdk

# Download and unpack sources
RUN apt-get update
RUN apt-get install ant -y
RUN apt-get install software-properties-common -y
RUN apt-get install libxrender1 libxtst6 libxi6 -y
RUN apt install -y cmake g++ wget unzip
RUN wget -O opencv.zip https://github.com/opencv/opencv/archive/master.zip
RUN wget -O opencv_contrib.zip https://github.com/opencv/opencv_contrib/archive/master.zip
RUN unzip opencv.zip
RUN unzip opencv_contrib.zip
# Create build directory and switch into it
RUN mkdir -p build && cd build
# Configure
RUN cmake -DOPENCV_EXTRA_MODULES_PATH=../opencv_contrib-master/modules ../opencv-master
# Build
RUN make install

COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","app.jar"]
