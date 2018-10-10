
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-functionName'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.getFunctionHelp()
    exit()

appStripe = argmap['appStripe']
functionName = argmap['functionName']

connect()
import Opss
Opss.getFunction(appStripe=appStripe, functionName=functionName)

