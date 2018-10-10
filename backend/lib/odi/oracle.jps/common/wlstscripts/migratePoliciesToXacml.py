

import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-src', '-dst', '-srcApp','-configFile'])
optional = frozenset(['-dstApp'])
import jpsCmdHelp
argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])
if argmap == None:
	jpsCmdHelp.migratePoliciesToXacmlHelp()
	exit()

src = argmap['src']
dst = argmap['dst']
configFile = argmap['configFile']
srcApp = argmap['srcApp']
dstApp = None


if 'dstApp' in argmap:
	dstApp = argmap['dstApp']

import Opss
Opss.migratePoliciesToXacml(src=src, dst=dst, srcApp=srcApp, dstApp=dstApp,configFile=configFile)
