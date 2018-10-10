const request = require('./auth.js').request
const ask = require('./lib/ask')
const _ = require('lodash')
const CLI = require('clui')
const config = require('config')
const fs = require('fs')
const dateFormat = require('dateformat');
const shell = require('shelljs')

const Spinner = CLI.Spinner

function findScenarios(options) {
  const { connectionId, payload, folderName, projectCode } = options
  return new Promise((resolve, reject) => {
    const url = `/misc/scenarios?connectionId=${connectionId}&projectCode=${projectCode}&folderName=${folderName}`
    return request.post(url, payload).then(response => {
      resolve(response.data)
    }, error => { reject(error) })
  })
}

function exportScenario(connectionId, payload) {
  return request.post(`/app/export?connectionId=${connectionId}`, payload)
}

const exportAllInFolder = async (options) => {
  const { connectionId } =  options
  const answer = await ask.exportParams()
  const payload = _.map(answer.types, t => {
    switch(t) {
      case 'Package scenarios': return 'package'
      case 'Procedure scenarios': return 'procedure'
      case 'Mapping scenarios': return 'mapping'
    }
  })
  let spinner = new Spinner('Finding scenarios ...', ['⣾','⣽','⣻','⢿','⡿','⣟','⣯','⣷']);
  spinner.start()
  const scenarios = await findScenarios({ connectionId, payload, ...answer})
  spinner.message(`Scenarios found.`)

  const exp = (s) => {
    const internalId = s.internalId
    const name = `${s.name}_Version_${s.version}`
    const payload = { internalId, name, ...config.export.options }
    spinner.message(`Exporting ${name} ...`)
    return request.post(`/app/export?connectionId=${connectionId}`, payload)
  }
  const time = dateFormat(new Date(), "yyyymmdd-HH-MM-ss");

  const st = _.chain(scenarios).keys().filter(k => !_.isEmpty(scenarios[k])).value()

  for (let k = 0; k < st.length; k++) {
    spinner.message(`Exporting ${scenarios[st[k]]} scenarios ...`)
    for(let i = 0; i < scenarios[st[k]].length; i++) {
      const s = scenarios[st[k]][i]
      const result = await exp(s)
      const content = result.data.content
      const path = `${config.export.path}/alchemist/export/${time}/${answer.folderName}/scenario/${st[k]}`
      await shell.mkdir('-p', path)
      fs.writeFileSync(`${path}/${s.name}.xml`, content, { flag: 'wx' })
    }
  }

  // _.chain(scenarios).keys().filter(k => !_.isEmpty(scenarios[k])).each(k => {
  //   spinner.message(`Exporting ${k} scenarios ...`)
  //   _.each(scenarios[k], s => {
  //   })
  // }).value()

  spinner.message("Export Done")
  spinner.stop()
  console.log(`Export finished.`)
  console.log(`Exported scenarios to ${config.export.path}/alchemist/export/${time}/${answer.folderName}/scenario`)
}

module.exports = {
  exportAllInFolder
}


