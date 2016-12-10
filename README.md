# Multiplayer Dungeon Game #

## To run primary server server ##
* Run serverMain 
  * with program argument for a port number


## To run a backup server ##
* Run serverMain
  * with program argument for a port number

## To compile client ##
* Run clientMain

- - - -
The first server opened is always primary and the second is backup, Once a primary crashes all backup tries to establish connection to it, if they fail to establish connection one of the backup will become a new primary. Once a primary fails all users are momentary disconnected and will automatically establish connection to the new primary server where they will be ask to re authenticate themselves.
- - - -

* __Note:__ If using eclipse, must add resource folder to configure the build path.
* __Note:__ All clients must have the ip and port of the servers in resource/serverList.txt
* __Note:__ All servers must have the ip and port of other servers in the resource/serverList.txt
  * __The list in resource/serverList.txt should be manually entered and consistent__



