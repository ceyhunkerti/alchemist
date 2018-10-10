
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-policyName', '-ruleExpression'])
optional = frozenset(['-displayName','-description','-entitlements', '-resourceActions', '-principals', '-codeSource', '-obligations', '-semantic', '-createNonExistingResource'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.createPolicyHelp()
    exit()

appStripe = argmap['appStripe']
policyName = argmap['policyName']
ruleExpression = argmap['ruleExpression']
displayName = None
description = None
entitlements = None
resourceActions = None
principals = None
codeSource = None
obligations = None
semantic = None
createNonExistingResource = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']
if 'entitlements' in argmap:
        entitlements = argmap['entitlements']
if 'resourceActions' in argmap:
        resourceActions = argmap['resourceActions']
if 'principals' in argmap:
        principals = argmap['principals']
if 'codeSource' in argmap:
        codeSource = argmap['codeSource']
if 'obligations' in argmap:
        obligations = argmap['obligations']
if 'semantic' in argmap:
        semantic = argmap['semantic']
if 'createNonExistingResource' in argmap:
        createNonExistingResource = argmap['createNonExistingResource']

connect()
import Opss
Opss.createPolicy(appStripe=appStripe, policyName=policyName, displayName=displayName, description=description, ruleExpression=ruleExpression, entitlements=entitlements, resourceActions=resourceActions, principals=principals, codeSource=codeSource, obligations=obligations, semantic=semantic, createNonExistingResource=createNonExistingResource)
