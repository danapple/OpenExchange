# Example:
# BBOX_PORT=/dev/ttyS0 make run

run: package
	java -jar ./exchange/target/openexchange-exchange-1.0-SNAPSHOT.jar

run-docker: docker
	docker run -p 5213:5213 openexchange:latest

package:
	mvn package

docker: package
	docker build --tag openexchange .

