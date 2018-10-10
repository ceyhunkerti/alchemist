
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-attributeName'])
optional = frozenset([])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.deleteAttributeHelp()
    exit()

appStripe = argmap['appStripe']
attributeName = argmap['attributeName']

connect()
import Opss
Opss.deleteAttribute(appStripe=appStripe, attributeName=attributeName)
