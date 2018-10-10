const inquirer = require('inquirer')


const connection = (connections) => {
  const q = [
    {
      type: 'list',
      name: 'name',
      message: 'Select a connection:',
      choices: connections
    }
  ]
  return inquirer.prompt(q)
}

const global = () => {
  const q = [
    {
      type: 'list',
      name: 'name',
      message: 'Select an option:',
      choices: [
        'Export Scenarios in Folder',
        'Exit'
      ]
    }
  ]
  return inquirer.prompt(q)
}

const exportParams = () => {
  const q = [
    {
      name: 'projectCode',
      type: 'input',
      message: 'Enter ODI project code:'
    },
    {
      name: 'folderName',
      type: 'input',
      message: 'Enter folder name:'
    },
    {
      type: 'checkbox',
      name: 'types',
      message: 'Select object types',
      choices: ['Package scenarios', 'Procedure scenarios', 'Mapping scenarios'],
      default: ['Procedure scenarios']
    }
  ]
  return inquirer.prompt(q)
}



module.exports = {
  connection,
  global,
  exportParams
}