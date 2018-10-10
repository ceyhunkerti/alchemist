const ask = require('./lib/ask')
const auth = require('./auth.js')
const config = require('config')
const connection = require('./connection')
const _ = require('lodash')
const chalk = require('chalk')
const clear = require('clear')
const figlet = require('figlet')
const CLI = require('clui')
const exp = require('./export.js')

const Spinner = CLI.Spinner

async function run () {

  clear()
  console.log(chalk.yellow(
    figlet.textSync('Alchemist', { horizontalLayout: 'full' })
  ))

  let spinner = new Spinner('Authenticating ...  ', ['⣾','⣽','⣻','⢿','⡿','⣟','⣯','⣷']);
  spinner.start()
  await auth.login(config.username, config.password)
  spinner.message("Authentication success.")
  spinner.stop()

  spinner.message('Finding connections ...')
  spinner.start()
  const connections = await connection.findAll()
  spinner.stop()

  let name = await ask.connection(_.map(connections, 'name'))
  const connectionId = _.find(connections, name ).id

  let opt = await ask.global()
  exp.exportAllInFolder({ connectionId })

}

run()

//! export icin payload
// connectionId: 7
// exportChildComponents: true
// exportKey: undefined
// internalId: 511
// javaCharSet: "ISO8859_1"
// name: "MA72IL_HASAR_SAGLIK_DETAY_Version_001"
// objectType: "SCENARIO"
// reExportKey: undefined
// xmlCharSet: "ISO-8859-1"
// xmlVersion: "1.0"