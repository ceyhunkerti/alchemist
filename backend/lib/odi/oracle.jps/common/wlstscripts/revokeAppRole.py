
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-appRoleName', '-principalClass', '-principalName'])
optional = frozenset(['-identityDomain'])
import jpsCmdHelp
argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])
if argmap == None:
	jpsCmdHelp.revokeAppRoleHelp()
	exit()

appStripe = argmap['appStripe']
appRoleName = argmap['appRoleName']
principalClass = argmap['principalClass']
principalName = argmap['principalName']
identityDomain = None

if 'identityDomain' in argmap:
    identityDomain = argmap['identityDomain']

connect()
import jpsWlstCmd
jpsWlstCmd.revokeAppRole(appStripe=appStripe, appRoleName=appRoleName, principalClass=principalClass, principalName=principalName, identityDomain=identityDomain)
