
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe'])
optional = frozenset([])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listPoliciesHelp()
    exit()

appStripe = argmap['appStripe']

connect()
import Opss
Opss.listPolicies(appStripe=appStripe)
