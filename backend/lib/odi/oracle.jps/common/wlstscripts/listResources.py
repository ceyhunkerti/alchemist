
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-type'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listResourcesHelp()
    exit()

appStripe = argmap['appStripe']
type = argmap['type']

connect()
import Opss
Opss.listResources(appStripe=appStripe, type=type)

