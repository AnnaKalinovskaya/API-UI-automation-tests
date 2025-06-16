package skelethon.requests;

import models.BaseModel;

public interface CRUD {

    public Object get();
    public Object post(BaseModel body);
    public Object put(BaseModel body);
    public Object delete(Long id);

}
