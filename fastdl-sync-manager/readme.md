# FastDL Sync Manager
This utility aims to provide the means of synchronizing a game server with a fastDL server (a server specialized in content delivery, versus your game server which may not provide the bandwidth or speed for file downloading). It automatically finds files that aren't on the fastDL server, BZips the file, then uploads it to the corresponding directory. Synced folders and file extensions can be customized to your preference.

### Features
 - Automatic Bzip2 compression
 - Abstractable to other game servers
 - Highly customizable settings
 - Watch folders
 - Amazon S3 Support

### Downloads
 - [Release 1.0.0](https://s3-us-west-1.amazonaws.com/fm-csgo/csgo-server-utils/fastdl-sync-manager-v1.0.0.zip)

### Usage/Running
A couple ways to run the manager.
 - Double click it.
   *Requires that the settings json file be named `fastdl_sync_manager_settings.json` and in the same directory as the jar.
 - Command line it.
   - No args uses the default `fastdl_sync_manager_settings.json` in the same directory as the jar.
   - 1+ args treats each argument as a settings file path and executes the program for every path given.
Hell, you can even make a bash/batch script to autoexecute it for you with the correct settings file path in place.

### Settings JSON
The settings JSON file holds all the settings for the manager (go figure). Here's a breakdown of what each setting does.
 - `serverCsgoFolder`: A path to the server's "csgo" (previously called "cstrike") folder. Works with relative paths too if that's your thing. This folder path will be the root of the fastDL server. *works with both unix and dos pathing.
 - `tempBzipFolder`: A path to a nonexistent folder which will be used as a temporary storage area.
 - `threadsToUseForCompression`: The number of threads you want to dedicate to Bzip compressing the files that need to be uploaded. Anything past the number of threads on your computer is redundant.
 - `upload`
   - `amazonS3`: See the configuration lower on this readme for a better explanation of these settings.
     - `enable`: [Boolean] If the Amazon S3 uploading should run.
	 - `keyId`: The IAM key ID.
	 - `secretKey`: The IAM secret key.
	 - `bucketName`: The bucket you want to throw the files into.
	 - `regionName`: The region name.
   - `ftp`: Ftp maybe? I dunno. I haven't implemented it yet though :P
 - `watchFolders`: An array of folders to watch. You can add as many folders as you want to this array.
     - `folderName`: The folder name to watch, relative to the `serverCsgoFolder`.
	 - `includeSubfolders`: [Boolean] If you want to include all subfolders within this folder.
	 - `includeExtentions`: An array that specifies the extensions to look for. This is a WHITELIST, thus, everything not listed is ignored.
	 - `ignoreFiles`: An array of paths relative to the `serverCsgoFolder` that denotes files that should be ignored that would otherwise be included by the `includeExtensions`.

###### Advanced
Because `serverCsgoFolder` just maps the game server directory to the root of the FastDL server, if you host multiple game servers on a single box, you can set `serverCsgoFolder` as a parent directory to all the game servers, and specify each game server's directories in the `watchFolders` array. Just food for thought.

### Amazon S3 Configuration
If you're wanting to use the Amazon S3 upload functionality, follow these steps! This guide assumes some knowledge of the AWS management console.  

1. Set up an S3 bucket  
   Add this permission to the bucket policy to allow public access to all files by default.
   ```
   {
       "Version": "2008-10-17",
       "Statement": [
           {
               "Sid": "AllowPublicRead",
               "Effect": "Allow",
               "Principal": {
                   "AWS": "*"
               },
               "Action": "s3:GetObject",
               "Resource": "arn:aws:s3:::bucket-name-here/*"
           }
       ]
   }
   ```
   Replace `bucket-name-here` with your actual bucket name.  
   Warning: THIS WILL MAKE ALL CONTENTS OF THAT BUCKET PUBLIC AND READ-ACCESSIBLE BY EVERYONE. If this isn't what you want to do with that bucket, you need to set permissions for each file/folder explicitly or through some other means.
2. Set up an IAM user for S3  
   Attach "AmazonS3FullAccess" policy to account (You can also make an IAM group and attach the group to the user. No preference from me which way you do.)  
   Be sure to copy/paste the key id and secret key into the json file.
3. Fill settings in the manager settings JSON file  
   Bucket region string can be found by accessing the bucket through the AWS console and looking in the URL for the parameter "region". It should look something like: `https://console.aws.amazon.com/s3/buckets/<bucket-name>?region=<region>`. Refer to [this](http://docs.aws.amazon.com/general/latest/gr/rande.html#s3_region) for valid S3 region names, under the "region" column.

### Troubleshooting
I tried to make the program as intuitive and easy to use as possible, but then again what programmer doesn't... The errors should be fairly self-explanitory. Just read 'em. If there's enough confusion about a specific error, I'll put it here.

 - `Unable to execute HTTP request: ...amazonaws.com`  
    Please check your bucket and region name. It's trying to access the url at `<bucket name>.s3.<region>.amazonaws.com` and can't.
 - `The AWS Access key id you provided does not exist in our records`  
    You suck at copy-pasting. Make sure the key id under amazonS3 settings is correct with the IAM user.
 - `The request signature we calculated does not match the signature you provided`  
    You suck at copy-pasting. Make sure the secret key under amazonS3 settings is correct with the IAM user.
   
### Changelog
Check the git milestone and release for all changelog information.

### Third Party
If you're interested in modifying the code, note that you need to download and include in your classpath the following libraries (and their dependencies):
 - [Amazon AWS Java SDK](https://aws.amazon.com/sdk-for-java/)
 - [Apache Commons Compress](https://commons.apache.org/proper/commons-compress/)
 - [Google gson](https://github.com/google/gson)