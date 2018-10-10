### Oracle Data Integrator Beautifier

Web application for oracle-data-integrator.

- Please contact using issues for more features.
- You can create a distribution binary and run in production mode.

#### Features

- Watch errors, running sessions, agents and data sources
- See running scripts, and exceptions messages
- Test agents and dataservers
- See # of running sessions on each agent
- Display load-plan history and statistics
- Desktop notifications and alers for errors
- Create multiple connections to different repositories
- TR and EN language support
- Zen mode in full screeen
- No need to install anything copy and run
- See [screenshots](https://github.com/bluecolor/alchemist/tree/master/screenshots)
- [CLI](cli/) bulk operations


#### ODI Versions

- For each ODI version you must provide the `odi-core.jar` and edit the [build.gradle](backend/build.gradle) if necessary
- Find `odi-core.jar` in your ODI installation folder
- Place your `odi-core.jar` under `alchemist/backend/lib`
- Remove any `odi-code.jar` if there is already one
- edit `build.gradle` like;
```groovy
    compile fileTree(include: [
    'bsh-2.0b4.jar',
    'identitystore.jar',
    'your-odi-code.jar', //! -----> this line
    'ojdl.jar',
    'dms.jar'
  ], dir: 'lib/odi')
```
- Build the project


#### Building

- `git clone git@github.com:bluecolor/alchemist.git`
- `cd backend & gradle bootrun`
- `cd frontend & npm install`
- `npm run dev`

#### Installation

- Download & unzip the project
- `cd alchemist/dist`
- `java -jar alchemist.jar`
- create system user using the `app shell`:
  ```
    ssh user@localhost -p 2000
    # enter password
    su -h # help for su command
    # example
    # su -m c -u system -p system -n system -e system@bluecolor.io
    # username: system password: system
  ```

- Goto browser and login with the credentions you gave eg. system/system