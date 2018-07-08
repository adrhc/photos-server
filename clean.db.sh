#!/bin/bash

# echo "albums:"
# mysql -u exifweb -pexifweb -e "select count(*) from Album;" exifweb-tests
# echo -e "\nimages:"
# mysql -u exifweb -pexifweb -e "select count(*) from Image;" exifweb-tests

mysql -u exifweb -pexifweb -e "update Album set FK_IMAGE = null;" exifweb-tests
mysql -u exifweb -pexifweb -e "delete from Image;" exifweb-tests
mysql -u exifweb -pexifweb -e "delete from Album;" exifweb-tests
mysql -u exifweb -pexifweb -e "delete from AppConfig;" exifweb-tests
mysql -u exifweb -pexifweb -e "delete from user;" exifweb-tests
