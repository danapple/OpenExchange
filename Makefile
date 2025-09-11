# Example:
# BBOX_PORT=/dev/ttyS0 make run

run: docker
	docker run openexchange:latest

package:
	mvn package

docker: package
	docker build --tag openexchange .

