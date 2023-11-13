if(redis.call('exists', KEYS[1]) == 0) then
    redis.call('set', KEYS[1], ARGV[1])
    redis.call('pexpire', KEYS[1], ARGV[2])
    return 1;
else
    return 0;
end


--eval "if(redis.call('exists', KEYS[1]) == 0) then redis.call('set', KEYS[1], ARGV[1]) redis.call('pexpire', KEYS[1], ARGV[2]) return 1; else return 0; end" 1 lockName uuid 30000


if(redis.call('exists', localName) == 0) then
    return 0;
end
if(redis.call('get', localName) == uuid) then
    redis.call('del', localName)
    return 1;
else
    return 0;
end


--eval "if(redis.call('exists', KEYS[1]) == 0) then return 0; end if(redis.call('get', KEYS[1]) == ARGV[1]) then redis.call('del', KEYS[1]) return 1; else return 0; end" 1 localName uuid


if(redis.call('exists', KEYS[1]) == 0) then
    redis.call('hincrby', KEYS[1], ARGV[1], 1)
    redis.call('pexpire', KEYS[1], ARGV[2])
    return 1;
end
if(redis.call('hexists', KEYS[1], ARGV[1]) == 1) then
    redis.call('hincrby', KEYS[1], ARGV[1], 1)
    redis.call('pexpire', KEYS[1], ARGV[2])
    return 1;
else
    return 0;
end


if(redis.call('hexists', KEYS[1], ARGV[1]) == 0) then
    return 0;
end
local localCount = redis.call('hincrby', KEYS[1], ARGV[1], -1)
if(localCount > 0) then
    redis.call('pexpire', KEYS[1], ARGV[2])
    return 1;
else
    redis.call('del', localName)
    return 1;
end