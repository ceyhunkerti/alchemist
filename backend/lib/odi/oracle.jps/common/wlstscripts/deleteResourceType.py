
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-resourceTypeName'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.deleteResourceTypeHelp()
    exit()

appStripe = argmap['appStripe']
resourceTypeName = argmap['resourceTypeName']

connect()
import jpsWlstCmd
jpsWlstCmd.deleteResourceType(appStripe=appStripe, resourceTypeName=resourceTypeName)

