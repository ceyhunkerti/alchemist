
import sys

from sets import ImmutableSet as frozenset
required = frozenset([])
optional = frozenset(['-appStripe','-policyDomainName'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.listAdminRolesHelp()
    exit()

appStripe = None
policyDomainName = None
if 'appStripe' in argmap:
        appStripe = argmap['appStripe']
if 'policyDomain' in argmap:
        policyDomainName = argmap['policyDomainName']

connect()
import Opss
Opss.listAdminRoles(appStripe=None, policyDomainName=None)
