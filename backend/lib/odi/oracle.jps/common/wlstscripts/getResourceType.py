
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-resourceTypeName'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.getResourceTypeHelp()
    exit()

appStripe = argmap['appStripe']
resourceTypeName = argmap['resourceTypeName']

connect()
import jpsWlstCmd
jpsWlstCmd.getResourceType(appStripe=appStripe, resourceTypeName=resourceTypeName)

