save = {}

---@param key string
---@param value integer 
function save.savei(key, value) end

---@param key string
---@param value number
function save.saved(key, value) end

---@param key string
---@param value boolean 
function save.saveb(key, value) end

---@param key string
---@param value string 
function save.saves(key, value) end

---@param key string
---@return integer 
function save.loadi(key) end

---@param key string
---@return number
function save.loadd(key) end

---@param key string
---@return boolean 
function save.loadb(key) end

---@param key string
---@return string 
function save.loads(key) end

---@param key string
---@return boolean 
function save.containsi(key) end

---@param key string
---@return boolean 
function save.containsd(key) end

---@param key string
---@return boolean 
function save.containsb(key) end

---@param key string
---@return boolean 
function save.containss(key) end