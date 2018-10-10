
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-name', '-type'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.deleteResourceHelp()
    exit()

appStripe = argmap['appStripe']
name = argmap['name']
type = argmap['type']

connect()
import Opss
Opss.deleteResource(appStripe=appStripe, name=name, type=type)

