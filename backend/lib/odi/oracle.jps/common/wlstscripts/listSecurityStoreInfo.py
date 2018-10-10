
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-domainConfig'])
optional = frozenset()
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listSecurityStoreInfoHelp()
    exit()

domainConfig=argmap['domainConfig']

import Opss
Opss.listSecurityStoreInfo(domainConfig=domainConfig)
