
import sys

from sets import ImmutableSet as frozenset
required = frozenset(['-appStripe', '-resourceTypeName'])
optional = frozenset(['-displayName', '-description', '-allowedActions', '-delimiter', '-attributes', '-provider', '-matcher', 'hierarchicalResource', 'resourceNameDelimiter'])
import jpsCmdHelp

argmap = jpsCmdHelp.verifyArgs(required, optional, sys.argv[1:])

if argmap == None:
    jpsCmdHelp.updateResourceTypeHelp()
    exit()

appStripe = argmap['appStripe']
resourceTypeName = argmap['resourceTypeName']
displayName = None
description = None
allowedActions = None
delimiter = None
attributes = None
provider = None
matcher = None
hierarchicalResource = None
resourceNameDelimiter = None

if 'displayName' in argmap:
        displayName = argmap['displayName']
if 'description' in argmap:
        description = argmap['description']
if 'allowedActions' in argmap:
        allowedActions = argmap['allowedActions']
if 'delimiter' in argmap:
        delimiter = argmap['delimiter']
if 'attributes' in argmap:
        attributes = argmap['attributes']
if 'provider' in argmap:
        provider = argmap['provider']
if 'matcher' in argmap:
        matcher = argmap['matcher']
if 'hierarchicalResource' in argmap:
        hierarchicalResource = argmap['hierarchicalResource']
if 'resourceNameDelimiter' in argmap:
        resourceNameDelimiter = argmap['resourceNameDelimiter']

connect()
import Opss
Opss.updateResourceType(appStripe=appStripe, resourceTypeName=resourceTypeName, displayName=displayName, description=description, allowedActions=allowedActions, delimiter=delimiter, attributes=attributes, provider=provider, matcher=matcher, hierarchicalResource=hierarchicalResource, resourceNameDelimiter=resourceNameDelimiter)
