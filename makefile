.PHONY: setup

setup: testing/python/_virtualenv testing/typescript

testing/python/_virtualenv:
	python3 -m venv testing/python/_virtualenv
	testing/python/_virtualenv/bin/pip install --upgrade pip
	testing/python/_virtualenv/bin/pip install --upgrade setuptools
	testing/python/_virtualenv/bin/pip install --upgrade wheel
	testing/python/_virtualenv/bin/pip install precisely==0.1.9 pytest==7.1.2

testing/typescript:

