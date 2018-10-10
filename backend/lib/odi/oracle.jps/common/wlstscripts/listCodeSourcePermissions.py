
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-codeBaseURL'])
optional = frozenset(['-appStripe'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listCodeSourcePermissionsHelp()
    exit()

appStripe = None
codeBaseURL = argmap['codeBaseURL']

if 'appStripe' in argmap:
    appStripe = argmap['appStripe']

connect()
import Opss
Opss.listCodeSourcePermissions(appStripe=appStripe, codeBaseURL=codeBaseURL)
