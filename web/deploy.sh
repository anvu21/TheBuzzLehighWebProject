# deploy script for the web front-end

# This file is responsible for preprocessing all TypeScript files, making sure
# all dependencies are up-to-date, and copying all necessary files into the
# web deploy directory.

# This is the resource folder where maven expects to find our files
TARGETFOLDER=../backend/src/main/resources

# This is the folder that we used with the Spark.staticFileLocation command
WEBFOLDERNAME=web

# step 1: delete the old folder tree, and then make it from scratch
rm -rf $TARGETFOLDER
mkdir $TARGETFOLDER
mkdir $TARGETFOLDER/$WEBFOLDERNAME

# copy an HTML file
cp index.html $TARGETFOLDER/$WEBFOLDERNAME

# step 2: update our npm dependencies
npm update

# step 3: copy jQuery, Handlebars, and Bootstrap files
cp node_modules/jquery/dist/jquery.min.js $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/handlebars/dist/handlebars.min.js $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/bootstrap/dist/js/bootstrap.min.js $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/bootstrap/dist/css/bootstrap.min.css $TARGETFOLDER/$WEBFOLDERNAME
cp -R node_modules/bootstrap/dist/fonts $TARGETFOLDER/$WEBFOLDERNAME

# step 4: compile TypeScript files
node_modules/typescript/bin/tsc app.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/app.js

# step 5: copy css files
cat css/*.css>  $TARGETFOLDER/$WEBFOLDERNAME/app.css

# step 6: compile handlebars templates to the deploy folder
for file in $(ls hb/*.hb); do
    node_modules/handlebars/bin/handlebars $file >> $TARGETFOLDER/$WEBFOLDERNAME/templates.js
done

# step 7: set up Jasmine
node_modules/typescript/bin/tsc apptest.ts --strict --outFile $TARGETFOLDER/$WEBFOLDERNAME/apptest.js 2>&1 1>/dev/null
cp spec_runner.html $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/jasmine-core/lib/jasmine-core/jasmine.css $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/jasmine-core/lib/jasmine-core/jasmine.js $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/jasmine-core/lib/jasmine-core/boot.js $TARGETFOLDER/$WEBFOLDERNAME
cp node_modules/jasmine-core/lib/jasmine-core/jasmine-html.js $TARGETFOLDER/$WEBFOLDERNAME
