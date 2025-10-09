---@class Path

---@class PathChain

follower = {};

---@return number
function follower.getPositionX() end

---@return number
function follower.getPositionY() end

---@return number
function follower.getPositionH() end

---@param x number
---@param y number
---@param h number
function follower.setPosition(x, y, h) end

---@param path Path
function follower.followPath(path) end

---@param path PathChain
function follower.followPathc(path) end

---@return boolean
function follower.isBusy() end

function follower.update() end
function follower.telem() end
function follower.initTelem() end


---@class PathBuilder
PathBuilder = {};

---@param curve Path
---@return PathBuilder
function PathBuilder:add(curve) end

---@param h number
---@return PathBuilder
function PathBuilder:constantHeading(h) end

---@param h1 number
---@param h2 number
---@return PathBuilder
function PathBuilder:linearHeading(h1, h2) end

---@param t number
---@param callback function
---@return PathBuilder
function PathBuilder:distanceCallback(t, callback) end

---@param t number
---@param callback function
---@return PathBuilder
function PathBuilder:timeCallback(t, callback) end

---@return PathChain
function PathBuilder:build() end

path = {};

---@param x1 number
---@param y1 number
---@param x2 number
---@param y2 number
---@return Path
function path.line(x1, y1, x2, y2) end

---@param x1 number
---@param y1 number
---@param x2 number
---@param y2 number
---@param x3 number
---@param y3 number
---@return Path
function path.curve3(x1, y1, x2, y2, x3, y3) end

---@param x1 number
---@param y1 number
---@param x2 number
---@param y2 number
---@param x3 number
---@param y3 number
---@param x4 number
---@param y4 number
---@return Path
function path.curve4(x1, y1, x2, y2, x3, y3, x4, y4) end

---@return PathBuilder
function path.chain() end