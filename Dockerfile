FROM            openjdk:oracle
RUN             mkdir /logon
WORKDIR         /logon 
RUN	            mkdir /logon/diaryImg/
COPY            ./build/libs/logon-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT      ["java", "-jar", "app.jar"]