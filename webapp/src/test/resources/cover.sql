update Album set FK_IMAGE = (select min(id) from Image where fk_album = 1) WHERE id = 1;
update Album set FK_IMAGE = (select min(id) from Image where fk_album = 2) WHERE id = 2;
