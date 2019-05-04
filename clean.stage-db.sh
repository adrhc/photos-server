#!/bin/bash

echo "albums:"
# mysql -u exifweb -pexifweb -e "select count(*) from Album;" exifweb-tests
mysql --login-path=local -e "select count(*) from Album;" exifweb-tests | grep -v count
echo -e "images:"
mysql --login-path=local -e "select count(*) from Image;" exifweb-tests | grep -v count
echo -e "appConfigs:"
mysql --login-path=local -e "select count(*) from AppConfig;" exifweb-tests | grep -v count
echo -e "group_members:"
mysql --login-path=local -e "select count(*) from group_members;" exifweb-tests | grep -v count
echo -e "users:"
mysql --login-path=local -e "select count(*) from user;" exifweb-tests | grep -v count
echo -e "group_authorities:"
mysql --login-path=local -e "select count(*) from group_authorities;" exifweb-tests | grep -v count
echo -e "groups:"
mysql --login-path=local -e "select count(*) from groups;" exifweb-tests | grep -v count

echo -e "\nupdate Album set FK_IMAGE = null"
mysql --login-path=local -e "update Album set FK_IMAGE = null;" exifweb-tests
echo -e "delete from Image"
mysql --login-path=local -e "delete from Image;" exifweb-tests
echo -e "delete from Album"
mysql --login-path=local -e "delete from Album;" exifweb-tests
echo -e "delete from AppConfig"
mysql --login-path=local -e "delete from AppConfig;" exifweb-tests
echo -e "delete from group_members"
mysql --login-path=local -e "delete from group_members;" exifweb-tests
echo -e "delete from user"
mysql --login-path=local -e "delete from user;" exifweb-tests
echo -e "delete from group_authorities"
mysql --login-path=local -e "delete from group_authorities;" exifweb-tests
echo -e "delete from groups"
mysql --login-path=local -e "delete from groups;" exifweb-tests

echo -e "\nremoved all data from: Album, Image, AppConfig, group_members, user, group_authorities, groups"
