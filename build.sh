sudo docker run --rm -ti -v $(pwd):/src -v $HOME/.m2:/root/.m2 -w /src maven:3-jdk-8 mvn clean package

