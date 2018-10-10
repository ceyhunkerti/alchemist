
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-principalClass', '-principalName'])
optional = frozenset(['-appStripe', '-identityDomain'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listPermissionsHelp()
    exit()

appStripe = None
principalClass = argmap['principalClass']
principalName = argmap['principalName']
identityDomain = None

if 'appStripe' in argmap:
    appStripe = argmap['appStripe']
if 'identityDomain' in argmap:
    identityDomain = argmap['identityDomain']

connect()
import jpsWlstCmd
jpsWlstCmd.listPermissions(appStripe=appStripe, principalClass=principalClass, principalName=principalName, identityDomain=identityDomain)
