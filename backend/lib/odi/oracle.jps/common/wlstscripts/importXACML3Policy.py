
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-configFile', '-appStripe', '-xacml3PolicyFile'])
optional = frozenset(['-dst', '-overWrite'])
import jpsCmdHelp
argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])
if argmap == None:
	jpsCmdHelp.importXACML3PolicyHelp()
	exit()

configFile = argmap['configFile']
dst = None
appStripe = argmap['appStripe']
xacml3PolicyFile = argmap['xacml3PolicyFile']
overWrite = None 

if 'dst' in argmap:
	dst = argmap['dst']

if 'overWrite' in argmap:
	overWrite = argmap['overWrite']

import Opss
Opss.importXACML3Policy(configFile=configFile, dst=dst, appStripe=appStripe, xacml3PolicyFile=xacml3PolicyFile, overWrite=overWrite)

