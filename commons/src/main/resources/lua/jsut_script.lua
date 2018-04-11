local A_startIndex = KEYS[1]
local A_stopIndex = KEYS[2]


local function createStringFunction(ctn)
    local function getString(field, default)
        return redis.call('hget', ctn .. ':prof', field) or default
    end
    return getString
end

local function createNumberFunction(ctn)
    local function getNumber(field, default)
        return tonumber(redis.call('hget', ctn .. ':prof', field)) or tonumber(default)
    end
    return getNumber
end

local result = {}
local ctnList = redis.call('zrange', "profiles", A_startIndex, A_stopIndex)

for ctnIndex = 1, #ctnList do
    local ctn = ctnList[ctnIndex]
    local s = createStringFunction(ctn)
    local n = createNumberFunction(ctn)

    local status, subsTargetingResult = pcall(function() {subs_targeting} end)
    if (status and subsTargetingResult) then
        table.insert(result, ctn)
    end
end

return result