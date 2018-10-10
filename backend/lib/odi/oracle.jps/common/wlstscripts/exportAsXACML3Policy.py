
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-configFile', '-appStripe', '-xacml3PolicyFile'])
optional = frozenset(['-src', '-policySetName', '-policyName'])
import jpsCmdHelp
argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])
if argmap == None:
	jpsCmdHelp.exportAsXACML3PolicyHelp()
	exit()

configFile = argmap['configFile']
src = None
appStripe = argmap['appStripe']
xacml3PolicyFile = argmap['xacml3PolicyFile']
policySetName = None 
policyName = None 

if 'src' in argmap:
	src = argmap['src']

if 'policySetName' in argmap:
	policySetName = argmap['policySetName']

if 'policyName' in argmap:
	policyName = argmap['policyName']

import Opss
Opss.exportAsXACML3Policy(configFile=configFile, src=src, appStripe=appStripe, xacml3PolicyFile=xacml3PolicyFile, policySetName=policySetName, policyName=policyName)

