
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-entitlementSetName'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listResourceActionsHelp()
    exit()

appStripe = argmap['appStripe']
entitlementSetName = argmap['entitlementSetName']

connect()
import Opss
Opss.listResourceActions(appStripe=appStripe, entitlementSetName=entitlementSetName)

