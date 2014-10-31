raspberry_bot
=========

raspberry_bot is a bot for creating clever home. It divided on a three parts:

  - Worker is special process for getting and processing informations and commands.
  - Site is a side for watching information about computer for clever home
  - Mobile application is application for sending commands for clever home on computer by email

It based on computer [Rasberry Pi B+]. Author is [Ilya Churaev].

Version
----

0.0.1

Installation
--------------

For auto start it is necessary to do:
```sh
sudo cp raspbery_server /etc/init.d/raspberry_server
sudo chmod +x /etc/init.d/raspberry_server
sudo update-rc.d raspberry_server defaults
```

##### Configure Plugins. Instructions in following README.md files

* plugins/dropbox/README.md
* plugins/github/README.md
* plugins/googledrive/README.md

```sh
node app
```


License
----

**Free Software, Hell Yeah!**

[Rasberry Pi B+]:http://www.raspberrypi.org/products/model-b-plus/
[Ilya Churaev]:https://github.com/ilyachur/