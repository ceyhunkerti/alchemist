
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listAppRolesHelp()
    exit()

appStripe = argmap['appStripe']

connect()
import jpsWlstCmd
jpsWlstCmd.listAppRoles(appStripe=appStripe)
