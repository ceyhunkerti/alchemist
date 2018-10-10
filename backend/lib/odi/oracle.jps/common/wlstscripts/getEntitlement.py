
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-name'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.getEntitlementHelp()
    exit()

appStripe = argmap['appStripe']
name = argmap['name']

connect()
import Opss
Opss.getEntitlement(appStripe=appStripe, name=name)

