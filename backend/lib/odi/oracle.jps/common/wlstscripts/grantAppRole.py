
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-appRoleName', '-principalClass', '-principalName'])
optional = frozenset(['-identityDomain'])
optional = frozenset(['-forceValidate'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.grantAppRoleHelp()
    exit()

appStripe = argmap['appStripe']
appRoleName = argmap['appRoleName']
principalClass = argmap['principalClass']
principalName = argmap['principalName']
forceValidate = "true"
if 'forceValidate' in argmap:
    forceValidate = argmap['forceValidate']
identityDomain = None

if 'identityDomain' in argmap:
    identityDomain = argmap['identityDomain']

connect()
import jpsWlstCmd
jpsWlstCmd.grantAppRole(appStripe=appStripe, appRoleName=appRoleName, principalClass=principalClass, principalName=principalName, identityDomain=identityDomain, forceValidate=forceValidate)
