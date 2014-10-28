#!/bin/bash
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  echo -e "Updating gh-pages\n"

  #copy data we're interested in to other place
  mkdir -p $HOME/reports
  cp -R build/reports/tests $HOME/reports

  #go to home and setup git
  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis"

  #using token clone gh-pages branch
  git clone --quiet --branch=master https://${GH_TOKEN}@github.com/greetlib/greetlib.github.io.git  gh-pages > /dev/null

  #go into diractory and copy data we're interested in to that directory
  cd gh-pages
  cp -Rf $HOME/reports .

  #add, commit and push files
  git add -f .
  git commit -m "Travis - Test results ${TRAVIS_REPO_SLUG}(${TRAVIS_COMMIT}) #${TRAVIS_BUILD_NUMBER}"
  git push -fq origin master > /dev/null

  echo -e "Pushed test reports.\n"
fi