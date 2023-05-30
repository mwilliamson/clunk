.PHONY: setup

setup: testing/java testing/python/_virtualenv testing/typescript

.PHONY: testing/java

testing/java: testing/java/lib/junit-platform-console-standalone-1.8.2.jar testing/java/lib/precisely-0.1.4.jar

testing/java/lib/junit-platform-console-standalone-1.8.2.jar:
	mkdir -p testing/java/lib
	wget -O $@ https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.8.2/junit-platform-console-standalone-1.8.2.jar

testing/java/lib/precisely-0.1.4.jar:
	mkdir -p testing/java/lib
	wget -O $@ https://github.com/mwilliamson/java-precisely/releases/download/0.1.4/precisely-0.1.4.jar

testing/python/_virtualenv:
	python3 -m venv testing/python/_virtualenv
	testing/python/_virtualenv/bin/pip install --upgrade pip
	testing/python/_virtualenv/bin/pip install --upgrade setuptools
	testing/python/_virtualenv/bin/pip install --upgrade wheel
	testing/python/_virtualenv/bin/pip install precisely==0.1.9 pytest==7.1.2

.PHONY: testing/typescript

testing/typescript:
	cd testing/typescript && npm install

mammoth/py/_virtualenv:
	python3 -m venv mammoth/py/_virtualenv
	mammoth/py/_virtualenv/bin/pip install --upgrade pip
	mammoth/py/_virtualenv/bin/pip install --upgrade setuptools
	mammoth/py/_virtualenv/bin/pip install --upgrade wheel
	mammoth/py/_virtualenv/bin/pip install -r mammoth/py/dev-requirements.txt

.PHONY: mammoth

mammoth: mammoth/py/_virtualenv
	java --enable-preview -jar target/clunk-1.0-SNAPSHOT.jar examples/mammoth --backend python --output mammoth/py/
	cd mammoth/py && PYTHONPATH=. _virtualenv/bin/py.test mammoth

.PHONY: package

package:
	mvn package -Dmaven.test.skip=true
