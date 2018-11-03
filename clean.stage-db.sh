#!/bin/bash

echo "albums:"
# mysql -u exifweb -pexifweb -e "select count(*) from Album;" exifweb-tests
mysql --login-path=local -e "select count(*) from Album;" exifweb-tests | grep -v count
echo -e "images:"
mysql --login-path=local -e "select count(*) from Image;" exifweb-tests | grep -v count
echo -e "appConfigs:"
mysql --login-path=local -e "select count(*) from AppConfig;" exifweb-tests | grep -v count
echo -e "users:"
mysql --login-path=local -e "select count(*) from user;" exifweb-tests | grep -v count

echo -e "update Album set FK_IMAGE = null:"
mysql --login-path=local -e "update Album set FK_IMAGE = null;" exifweb-tests
echo -e "delete from Image:"
mysql --login-path=local -e "delete from Image;" exifweb-tests
echo -e "delete from Album:"
mysql --login-path=local -e "delete from Album;" exifweb-tests
echo -e "delete from AppConfig:"
mysql --login-path=local -e "delete from AppConfig;" exifweb-tests
echo -e "delete from user:"
mysql --login-path=local -e "delete from user;" exifweb-tests

echo "removed all data from: Album, Image, AppConfig, user"
