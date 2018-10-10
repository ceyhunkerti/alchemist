
import sys

from sets import ImmutableSet as frozenset
required = frozenset()
optional = frozenset(['-configFile', '-regularExpression'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listAppStripesHelp()
    exit()

configFile = None
regularExpression = None
if 'regularExpression' in argmap:
    regularExpression = argmap['regularExpression']
if 'configFile' in argmap:
    configFile = argmap['configFile']

if (configFile is None):
    connect()
import Opss
Opss.listAppStripes(configFile=configFile, regularExpression=regularExpression)

