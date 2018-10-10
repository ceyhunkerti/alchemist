
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-principalClass', '-principalName'])
optional = frozenset(['-identityDomain'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listGrantedEntitlementsHelp()
    exit()

appStripe = argmap['appStripe']
principalClass = argmap['principalClass']
principalName = argmap['principalName']
identityDomain = None

if 'identityDomain' in argmap:
    identityDomain = argmap['identityDomain']

connect()
import jpsWlstCmd
jpsWlstCmd.listGrantedEntitlements(appStripe=appStripe, principalClass=principalClass, principalName=principalName, identityDomain=identityDomain)
