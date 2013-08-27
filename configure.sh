#-------------------------------------------------------------------------------
# Copyright (C) 2008-2010 Surevine Limited.
#   
# Although intended for deployment and use alongside Alfresco this module should
# be considered 'Not a Contribution' as defined in Alfresco'sstandard contribution agreement, see
# http://www.alfresco.org/resource/AlfrescoContributionAgreementv2.pdf
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#-------------------------------------------------------------------------------
#!/bin/sh

# Performs property file modifications

# Abort on error
set -e

# set constant file names
SHARE_DIR=tomcat/webapps/share
ALFRESCO_DIR=tomcat/webapps/alfresco
CUR_DIR=`pwd`

PROPS_FILE=`echo $CUR_DIR/configuration.properties`
if [ ! -f $PROPS_FILE ]
then
    echo "The specified properties file $PROPS_FILE does not exist in the current directory"
    exit 1
fi

echo "    Checking share directory exists"
if [ ! -d $SHARE_DIR ]
then
    echo "Usage: The $SHARE_DIR directory is expected"
    exit 1
fi

echo "    Checking alfresco directory exists"
if [ ! -d $ALFRESCO_DIR ]
then
    echo "Usage: The $ALFRESCO_DIR directory is expected"
    exit 1
fi

tokenised_share_files=(
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/discussions/createtopic.get.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/upload/flash-upload.get.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/upload/html-upload.get.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/wiki/createform.get.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/wiki/page.get.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/modules/discussions/replies/reply-form.get.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/enhanced-security/enhanced-security.get.js'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/discussions/createtopic.get_en.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/upload/html-upload.get_en.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/wiki/createform.get_en.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/wiki/page.get_en.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/modules/discussions/replies/reply-form.get_en.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/enhanced-security/selector/enhanced-security-selector.get.js'
    'components/enhanced-security/selector/enhanced-security-static-data-min.js'
    'components/enhanced-security/selector/enhanced-security-static-data.js'
    'components/enhanced-security/selector/enhanced-security-selector-min.js'
    'components/enhanced-security/selector/enhanced-security-selector.js'
    'components/enhanced-security/selector/visibility-utils-min.js'
    'components/enhanced-security/selector/visibility-utils.js'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/enhanced-security/lib/enhanced-security.lib.js'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/enhanced-security/selector/enhanced-security-selector.get.properties'
    'WEB-INF/classes/alfresco/site-webscripts/org/alfresco/components/enhanced-security/selector/enhanced-security-selector.get_en.properties'
    
    )

tokenised_alfresco_files=(
    'WEB-INF/classes/alfresco/templates/webscripts/org/alfresco/slingshot/enhanced-security/lib/enhanced-security.lib.js'
  )

# Extra css file to be extracted form the share.war file
CSS_FILE="components/enhanced-security/enhanced-security.css"

echo "Customising provided application..."
echo "    Customising Share"
cd $SHARE_DIR
for FILE in ${tokenised_share_files[@]}
do
    mv ${FILE} ${FILE}.orig
    sed -f ${PROPS_FILE} ${FILE}.orig > ${FILE}
    #rm ${FILE}.orig
done

# When replacing on the CSS file, we only use the markings and remove spaces from the values 
mv ${CSS_FILE} ${CSS_FILE}.orig
cat < ${PROPS_FILE} | grep esc..marking | sed 's/ //g' > ${PROPS_FILE}.css
sed -f ${PROPS_FILE}.css ${CSS_FILE}.orig > ${CSS_FILE}

cd - > /dev/null

echo "    Customising Alfresco"
# Extra JAR file to be extracted from the alfresco.war file
cd $ALFRESCO_DIR
for FILE in ${tokenised_alfresco_files[@]}
do
    mv ${FILE} ${FILE}.orig
    sed -f ${PROPS_FILE} ${FILE}.orig > ${FILE}
    sed -i s/@@CAS_HOSTNAME@@/${GUESSED_IP}/g ${FILE}
    sed -i s/@@SHARE_HOSTNAME@@/${GUESSED_IP}/g ${FILE}
    #rm ${FILE}.orig
done
cd -


echo "Application customised"
exit 0
