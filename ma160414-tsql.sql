USE [ma160414]
GO

/****** Object:  Trigger [dbo].[TR_TransportOffer_InsertZahtevPrevoz]    Script Date: 16-Jul-20 1:48:23 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE TRIGGER [dbo].[TR_TransportOffer_InsertZahtevPrevoz]
ON [dbo].[Paket]
AFTER INSERT
AS
BEGIN

	DECLARE @polaziste int
	DECLARE @odrediste int
	DECLARE @tezina decimal(10,3)
	DECLARE @tip int
	DECLARE @cena decimal(10,3)
	DECLARE @idPaketa int

	SELECT @idPaketa = ID, @polaziste = Polaziste, @odrediste = Odrediste, @tezina = Tezina, @cena = Cena, @tip = Tip
	FROM inserted

	DECLARE @x1 decimal(10,3)
	DECLARE @x2 decimal(10,3)
	DECLARE @y1 decimal(10,3)
	DECLARE @y2 decimal(10,3)

	SELECT @x1 = X, @y1 = Y FROM [Adresa] WHERE ID = @polaziste
	SELECT @x2 = X, @y2 = Y FROM [Adresa] WHERE ID = @odrediste

	DECLARE @cenaKG int

	IF @tip = 0
	BEGIN
		SET @cenaKG = 0
	END
	ELSE IF @tip = 1
	BEGIN
		SET @cenaKG = 100
	END
	ELSE IF @tip = 2
	BEGIN
		SET @cenaKG = 100
	END
	ELSE IF @tip = 3
	BEGIN
		SET @cenaKG = 500
	END

	DECLARE @distanca decimal(10,3)
	SET @distanca = SQRT(POWER((@x1-@x2), 2) + POWER((@y1-@y2), 2))

	DECLARE @cenaIsporuke decimal(10,3)
	SET @cenaIsporuke = (@cena + @tezina * @cenaKG) * @distanca

	INSERT INTO [ZahtevPrevoz] (Paket, CenaIsporuke, Status)
	VALUES (@idPaketa, @cenaIsporuke, 0)

END
GO

ALTER TABLE [dbo].[Paket] ENABLE TRIGGER [TR_TransportOffer_InsertZahtevPrevoz]
GO

/****** Object:  Trigger [dbo].[TR_TransportOffer_UpdateZahtevPrevoz]    Script Date: 16-Jul-20 1:49:23 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE TRIGGER [dbo].[TR_TransportOffer_UpdateZahtevPrevoz]
ON [dbo].[Paket]
AFTER UPDATE
AS
BEGIN

	DECLARE @polaziste int
	DECLARE @odrediste int
	DECLARE @tezina decimal(10,3)
	DECLARE @tip int
	DECLARE @cena decimal(10,3)
	DECLARE @idPaketa int

	SELECT @idPaketa = ID, @polaziste = Polaziste, @odrediste = Odrediste, @tezina = Tezina, @cena = Cena, @tip = Tip
	FROM inserted

	DECLARE @x1 decimal(10,3)
	DECLARE @x2 decimal(10,3)
	DECLARE @y1 decimal(10,3)
	DECLARE @y2 decimal(10,3)

	SELECT @x1 = X, @y1 = Y FROM [Adresa] WHERE ID = @polaziste
	SELECT @x2 = X, @y2 = Y FROM [Adresa] WHERE ID = @odrediste

	DECLARE @cenaKG int

	IF @tip = 0
	BEGIN
		SET @cenaKG = 0
	END
	ELSE IF @tip = 1
	BEGIN
		SET @cenaKG = 100
	END
	ELSE IF @tip = 2
	BEGIN
		SET @cenaKG = 100
	END
	ELSE IF @tip = 3
	BEGIN
		SET @cenaKG = 500
	END

	DECLARE @distanca decimal(10,3)
	SET @distanca = SQRT(POWER((@x1-@x2), 2) + POWER((@y1-@y2), 2))

	DECLARE @cenaIsporuke decimal(10,3)
	SET @cenaIsporuke = (@cena + @tezina * @cenaKG) * @distanca

	UPDATE [ZahtevPrevoz] SET CenaIsporuke = @cenaIsporuke WHERE Paket = @idPaketa

END
GO

ALTER TABLE [dbo].[Paket] ENABLE TRIGGER [TR_TransportOffer_UpdateZahtevPrevoz]
GO

/****** Object:  StoredProcedure [dbo].[spDeclareAdmin]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spDeclareAdmin]
@korisnickoIme varchar(100),
@ret bit output
AS
BEGIN

	IF @korisnickoIme IS NULL OR @korisnickoIme = ''
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Korisnik] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF EXISTS (SELECT * FROM [Administrator] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		RETURN
	END

	INSERT INTO [Administrator] VALUES(@korisnickoIme)

	SET @ret = 1

	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spDeleteLoakacijaMagacina]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE [dbo].[spDeleteLoakacijaMagacina]
	@adresa int,
	@ret bit output
AS
BEGIN

	IF @adresa IS NULL
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [LokacijaMagacina] WHERE Adresa = @adresa)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF (SELECT COUNT(*) FROM [ParkiranoVozilo] WHERE Magacin = @adresa) <> 0 
	BEGIN
		SET @ret = 0
		RETURN
	END

	DELETE FROM [LokacijaMagacina] WHERE Adresa = @adresa

	SET @ret = 1
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spDeleteLoakacijaMagacinaIzGrada]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE [dbo].[spDeleteLoakacijaMagacinaIzGrada]
	@grad int,
	@ret int output
AS
BEGIN

	IF @grad IS NULL
	BEGIN
		SET @ret = -1
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Grad] WHERE ID = @grad)
	BEGIN
		SET @ret = -1
		RETURN
	END

	IF NOT EXISTS (SELECT * 
		FROM [LokacijaMagacina] LM INNER JOIN [Adresa] A ON LM.Adresa = A.ID
		WHERE A.Grad = @grad)
	BEGIN
		SET @ret = -1
		RETURN
	END

	IF EXISTS (SELECT * FROM [LokacijaMagacina] LM 
		INNER JOIN [Adresa] A ON LM.Adresa = A.ID
		INNER JOIN [ParkiranoVozilo] P ON LM.Adresa = P.Magacin)
	BEGIN
		SET @ret = -1
		RETURN
	END

	SELECT @ret = ID FROM Adresa WHERE Grad = @grad

	DELETE FROM [LokacijaMagacina] WHERE Adresa = @ret

	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spDodeliVozilo]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[spDodeliVozilo]
	@user varchar(100),
	@reg varchar(100),
	@ret bit output
AS
BEGIN
	
	IF @user IS NULL OR @user = '' OR
	@reg IS NULL OR @reg = ''
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [ParkiranoVozilo] WHERE RegistracioniBroj = @reg)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF EXISTS (SELECT * FROM [Kurir] WHERE KorisnickoIme = @user AND Status = 1)
	BEGIN
		SET @ret = 0
		RETURN
	END

	DELETE FROM [ParkiranoVozilo] WHERE RegistracioniBroj = @reg

	UPDATE [Kurir] SET Status = 1, Vozi = @reg WHERE KorisnickoIme = @user

	IF NOT EXISTS (SELECT * FROM [KoristioVozilo] WHERE KorisnickoIme = @user AND RegistracioniBroj = @reg)
	BEGIN
		INSERT INTO [KoristioVozilo] (KorisnickoIme, RegistracioniBroj) VALUES (@user, @reg)
	END

	SET @ret = 1
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spDohvatiVozilo]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[spDohvatiVozilo]
	@user varchar(100),
	@ret bit output,
	@reg varchar(100) output
AS
BEGIN
	
	IF @user IS NULL OR @user = ''
	BEGIN
		SET @ret = 0
		SET @reg = '-1'
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Kurir] WHERE KorisnickoIme = @user)
	BEGIN
		SET @ret = 0
		SET @reg = '-1'
		RETURN
	END

	IF EXISTS (
		SELECT PV.RegistracioniBroj
		FROM [ParkiranoVozilo] PV, Adresa A, Korisnik K
		WHERE PV.Magacin = A.ID AND K.KorisnickoIme=@user AND A.Grad IN 
		(SELECT AA.Grad FROM Adresa AA, Korisnik KK WHERE KK.Adresa = AA.ID AND KK.KorisnickoIme=@user)
	)
	BEGIN
		SELECT TOP 1 @reg = PV.RegistracioniBroj
		FROM [ParkiranoVozilo] PV, Adresa A, Korisnik K
		WHERE PV.Magacin = A.ID AND K.KorisnickoIme=@user AND A.Grad IN 
		(SELECT AA.Grad FROM Adresa AA, Korisnik KK WHERE KK.Adresa = AA.ID AND KK.KorisnickoIme=@user)

		SET @ret = 1
		RETURN
	END

	SET @ret = 0
	SET @reg = '-1'
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spEraseAll]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- ================================================
-- Template generated from Template Explorer using:
-- Create Procedure (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 

CREATE PROCEDURE [dbo].[spEraseAll]
AS
BEGIN
	DELETE FROM [ZahtevPrevoz]
	DELETE FROM [ParkiranoVozilo]
	DELETE FROM [ZahtevKurir]
	DELETE FROM [KoristioVozilo]
	DELETE FROM [Paket]
	DELETE FROM [Administrator]
	DELETE FROM [Kupac]
	DELETE FROM [Kurir]
	DELETE FROM [Vozilo]
	DELETE FROM [Korisnik]
	DELETE FROM [LokacijaMagacina]
	DELETE FROM [Adresa]
	DELETE FROM [Grad]
END
GO
/****** Object:  StoredProcedure [dbo].[spInsertAdresa]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spInsertAdresa]
@Ulica varchar(100),
@Broj varchar(100),
@idGrad int,
@x decimal(10,3),
@y decimal(10,3),
@id_out int output,
@msg varchar(100) output
AS
BEGIN
	
	IF @Ulica = '' OR @Ulica IS NULL OR @Broj = '' OR @Broj IS NULL OR @x IS NULL OR @y IS NULL
	BEGIN
		SET @id_out = -1
		SET @msg = 'param null adresa'
		RETURN
	END

	IF @idGrad IS NULL OR @idGrad = -1
	BEGIN
		SET @id_out = -1
		SET @msg = 'grad null adresa'
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Grad] WHERE ID = @idGrad)
	BEGIN
		SET @id_out = -1
		SET @msg = 'grad ne postoji adresa'
		RETURN
	END

	IF EXISTS (SELECT * FROM [Adresa] WHERE Grad = @idGrad AND X = @x AND Y=@y)
	BEGIN
		SET @id_out = -1
		SET @msg = 'postoji adresa'
		RETURN
	END

	INSERT INTO [Adresa](Ulica, Broj, Grad, X, Y) VALUES (@Ulica, @Broj, @idGrad, @x, @y)

	SELECT @id_out = Adresa.ID FROM [Adresa] WHERE Grad = @idGrad AND X = @x AND Y=@y

	SET @msg = 'OK adresa'

	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spInsertKurir]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spInsertKurir]
@korisnickoIme varchar(100),
@brojDozvole varchar(100),
@ret bit output
AS
BEGIN

	IF @korisnickoIme IS NULL OR @korisnickoIme = '' OR 
		@brojDozvole IS NULL OR @brojDozvole = ''
	BEGIN
		SET @ret = 0
		RETURN
	END
	
	IF NOT EXISTS (SELECT * FROM [Korisnik] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF EXISTS (SELECT * FROM [Kurir] WHERE KorisnickoIme = @korisnickoIme OR BrojDozvole = @brojDozvole)
	BEGIN
		SET @ret = 0
		RETURN
	END

	INSERT INTO [Kurir](KorisnickoIme, Vozi, BrojIsprucenihPaketa, Profit, Status, BrojDozvole)
	VALUES(@korisnickoIme, null, 0, 0, 0, @brojDozvole)

	SET @ret = 1
	
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spInsertLokacijaMagacina]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[spInsertLokacijaMagacina] 
	@adresa int,
	@ret int output
AS
BEGIN
	
	IF @adresa IS NULL
	BEGIN
		SET @ret = -1
		RETURN
	END

	IF EXISTS (SELECT * FROM [LokacijaMagacina] WHERE Adresa = @adresa)
	BEGIN
		SET @ret = -1
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Adresa] WHERE ID = @adresa)
	BEGIN
		SET @ret = -1
		RETURN
	END

	DECLARE @grad int

	SELECT @grad = Grad FROM [Adresa] WHERE ID = @adresa

	IF EXISTS (SELECT * FROM [LokacijaMagacina] LM
		INNER JOIN [Adresa] A ON LM.Adresa = A.ID
		WHERE A.Grad = @grad)
	BEGIN
		SET @ret = -1
		RETURN
	END

	INSERT INTO [LokacijaMagacina] (Adresa) VALUES (@adresa)

	SET @ret = @adresa
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spInsertPaket]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE PROC [dbo].[spInsertPaket]
@polaziste int,
@odrediste int,
@korisnik varchar(100),
@tip int,
@tezina decimal(10,3),
@ret int output
AS
BEGIN

	IF @polaziste IS NULL OR @odrediste IS NULL OR 
		@korisnik IS NULL OR @korisnik = '' OR
		@tip IS NULL OR @tezina IS NULL OR
		@tip < 0 OR @tip > 3 OR @tezina < 0
	BEGIN
		SET @ret = -1
		RETURN
	END
	
	IF NOT EXISTS (SELECT * FROM [Adresa] WHERE ID = @polaziste)
	BEGIN
		SET @ret = -1
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Adresa] WHERE ID = @odrediste)
	BEGIN
		SET @ret = -1
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Korisnik] WHERE KorisnickoIme = @korisnik)
	BEGIN
		SET @ret = -1
		RETURN
	END

	DECLARE @cena int

	IF @tip = 0
	BEGIN
		SET @cena = 115
	END
	ELSE IF @tip = 1
	BEGIN
		SET @cena = 175
	END
	ELSE IF @tip = 2
	BEGIN
		SET @cena = 250
	END
	ELSE IF @tip = 3
	BEGIN
		SET @cena = 350
	END

	--(0 – „zahtev kreiran“, 1 – „prihvaćena ponuda“, 2 - „paket preuzet“, 3 – „isporučen“, 4 - „ponuda odbijena“)
	INSERT INTO [Paket](Status, VremeKreiranja, VremePrihvatanja, TrenutnaLokacija, Odrediste, Polaziste, Korisnik, Tip, Tezina, Cena, UVozilu, UMagacinu)
	VALUES(0, GETDATE(), null,  @polaziste, @odrediste, @polaziste, @korisnik, @tip, @tezina, @cena, 0, 0)

	SELECT @ret = IDENT_CURRENT('Paket')
	
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spInsertUser]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spInsertUser]
@korisnickoIme varchar(100),
@ime varchar(100),
@prezime varchar(100),
@sifra varchar(100),
@adresa int,
@ret bit output,
@message varchar(100) output
AS
BEGIN

	IF @korisnickoIme IS NULL OR @korisnickoIme = '' OR 
		@ime IS NULL OR @ime = '' OR
		@prezime IS NULL OR @prezime = '' OR
		@sifra IS NULL OR @sifra = '' OR
		@adresa IS NULL
	BEGIN
		SET @ret = 0
		SET @message = 'param is null or empty.'
		RETURN
	END
	
	IF EXISTS (SELECT * FROM [Korisnik] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		SET @message = 'korisnik postoji'
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM Adresa WHERE ID = @adresa)
	BEGIN
		SET @ret = 0
		SET @message = 'nema adrese'
		RETURN
	END

	INSERT INTO [Korisnik](KorisnickoIme, Ime, Prezime, Sifra, Adresa)
	VALUES(@korisnickoIme, @ime, @prezime, @sifra, @adresa)

	SET @ret = 1
	SET @message = 'ok'
	
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spInsertVozilo]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spInsertVozilo]
@registracioniBroj varchar(100),
@tipGoriva int,
@potrosnja decimal(10,3),
@nosivost decimal(10,3),
@ret bit output
AS
BEGIN

	IF @registracioniBroj IS NULL OR @registracioniBroj = '' OR 
		@tipGoriva IS NULL OR @tipGoriva < 0 OR @tipGoriva > 2 OR
		@potrosnja IS NULL OR @potrosnja < 0 OR
		@nosivost IS NULL OR @nosivost < 0
	BEGIN
		SET @ret = 0
		RETURN
	END
	
	IF EXISTS (SELECT * FROM [Vozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @ret = 0
		RETURN
	END

	INSERT INTO [Vozilo](RegistracioniBroj, TipGoriva, Potrosnja, Nosivost)
	VALUES(@registracioniBroj, @tipGoriva, @potrosnja, @nosivost)

	SET @ret = 1
	
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spInsertZahtevKurir]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spInsertZahtevKurir]
@korisnickoIme varchar(100),
@brojDozvole varchar(100),
@ret bit output
AS
BEGIN

	IF @korisnickoIme IS NULL OR @korisnickoIme = '' OR 
		@brojDozvole IS NULL OR @brojDozvole = ''
	BEGIN
		SET @ret = 0
		RETURN
	END
	
	IF NOT EXISTS (SELECT * FROM [Korisnik] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF EXISTS (SELECT * FROM [Kurir] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF EXISTS (SELECT * FROM [ZahtevKurir] WHERE KorisnickoIme = @korisnickoIme OR BrojDozvole = @brojDozvole)
	BEGIN
		SET @ret = 0
		RETURN
	END

	INSERT INTO [ZahtevKurir](KorisnickoIme, BrojDozvole)
	VALUES(@korisnickoIme, @brojDozvole)

	SET @ret = 1
	
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spOdbijPonudu]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spOdbijPonudu]
@paket int,
@ret bit output
AS
BEGIN
	
	IF @paket IS NULL
	BEGIN
		SET @ret = 0
		RETURN
	END

	-- (0 – „zahtev kreiran“, 1 – „prihvaćena ponuda“, 2 - „paket preuzet“, 3 – „isporučen“, 4 - „ponuda odbijena“)
	IF NOT EXISTS (SELECT * FROM [Paket] WHERE ID = @paket AND Status = 0)
	BEGIN
		SET @ret = 0
		RETURN
	END

	-- 0 - cekanje; 1 - prihvaceno; 2 - odbijeno
	UPDATE [ZahtevPrevoz]
	SET Status = 2
	WHERE Paket = @paket

	UPDATE [Paket]
	SET Status = 4, VremePrihvatanja = GETDATE()
	WHERE ID = @paket

	SET @ret = 1
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spParkiranjeVozila]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spParkiranjeVozila]
@registracioniBroj varchar(100),
@magacin int,
@ret bit output
AS
BEGIN

	IF @registracioniBroj IS NULL OR @registracioniBroj = '' OR 
		@magacin IS NULL
	BEGIN
		SET @ret = 0
		RETURN
	END
	
	IF NOT EXISTS (SELECT * FROM [LokacijaMagacina] WHERE Adresa = @magacin)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Vozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF EXISTS (SELECT * FROM [ParkiranoVozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @ret = 0
		RETURN
	END

	INSERT INTO [ParkiranoVozilo](RegistracioniBroj, Magacin)
	VALUES(@registracioniBroj, @magacin)

	SET @ret = 1
	
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spPrihvatiPonudu]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spPrihvatiPonudu]
@paket int,
@ret bit output
AS
BEGIN
	
	IF @paket IS NULL
	BEGIN
		SET @ret = 0
		RETURN
	END

	-- (0 – „zahtev kreiran“, 1 – „prihvaćena ponuda“, 2 - „paket preuzet“, 3 – „isporučen“, 4 - „ponuda odbijena“)
	IF NOT EXISTS (SELECT * FROM [Paket] WHERE ID = @paket AND Status = 0)
	BEGIN
		SET @ret = 0
		RETURN
	END

	-- 0 - cekanje; 1 - prihvaceno; 2 - odbijeno
	UPDATE [ZahtevPrevoz]
	SET Status = 1
	WHERE Paket = @paket

	UPDATE [Paket]
	SET Status = 1, VremePrihvatanja = GETDATE()
	WHERE ID = @paket

	SET @ret = 1
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spPrihvatiZahtevKurir]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[spPrihvatiZahtevKurir]
@korisnickoIme varchar(100),
@ret bit output
AS
BEGIN

	IF @korisnickoIme IS NULL OR @korisnickoIme = ''
	BEGIN
		SET @ret = 0
		RETURN
	END
	
	IF NOT EXISTS (SELECT * FROM [Korisnik] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [ZahtevKurir] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		RETURN
	END

	IF EXISTS (SELECT * FROM [Kurir] WHERE KorisnickoIme = @korisnickoIme)
	BEGIN
		SET @ret = 0
		RETURN
	END

	DECLARE @brojDozvole varchar(100);

	SELECT @brojDozvole = BrojDozvole FROM [ZahtevKurir] WHERE KorisnickoIme = @korisnickoIme;

	INSERT INTO [Kurir](KorisnickoIme, Vozi, BrojIsprucenihPaketa, Profit, Status, BrojDozvole)
	VALUES(@korisnickoIme, null, 0, 0, 0, @brojDozvole)

	DELETE FROM [ZahtevKurir] WHERE KorisnickoIme = @korisnickoIme;

	SET @ret = 1
	
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spPromenaNosivostiVozilo]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROC [dbo].[spPromenaNosivostiVozilo]
@registracioniBroj varchar(100),
@nosivost decimal(10,3),
@res bit output
AS
BEGIN

	IF @registracioniBroj IS NULL OR @registracioniBroj = ''
	BEGIN
		SET @res = 0
		RETURN
	END

	IF @nosivost IS NULL OR @nosivost < 0
	BEGIN
		SET @res = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Vozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @res = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [ParkiranoVozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @res = 0
		RETURN
	END

	UPDATE [Vozilo]
	SET Nosivost = @nosivost
	WHERE RegistracioniBroj = @registracioniBroj

	SET @res = 1

	RETURN 
END
GO
/****** Object:  StoredProcedure [dbo].[spPromenaPotrosnjeVozilo]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROC [dbo].[spPromenaPotrosnjeVozilo]
@registracioniBroj varchar(100),
@potrosnja decimal(10,3),
@res bit output
AS
BEGIN

	IF @registracioniBroj IS NULL OR @registracioniBroj = ''
	BEGIN
		SET @res = 0
		RETURN
	END

	IF @potrosnja IS NULL OR @potrosnja < 0
	BEGIN
		SET @res = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Vozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @res = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [ParkiranoVozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @res = 0
		RETURN
	END

	UPDATE [Vozilo]
	SET Potrosnja = @potrosnja
	WHERE RegistracioniBroj = @registracioniBroj

	SET @res = 1

	RETURN 
END
GO
/****** Object:  StoredProcedure [dbo].[spPromenaTipaGorivaVozilo]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROC [dbo].[spPromenaTipaGorivaVozilo]
@registracioniBroj varchar(100),
@tip int,
@res bit output
AS
BEGIN

	IF @registracioniBroj IS NULL OR @registracioniBroj = ''
	BEGIN
		SET @res = 0
		RETURN
	END

	IF @tip IS NULL OR @tip < 0 OR @tip > 2
	BEGIN
		SET @res = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [Vozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @res = 0
		RETURN
	END

	IF NOT EXISTS (SELECT * FROM [ParkiranoVozilo] WHERE RegistracioniBroj = @registracioniBroj)
	BEGIN
		SET @res = 0
		RETURN
	END

	UPDATE [Vozilo]
	SET TipGoriva = @tip
	WHERE RegistracioniBroj = @registracioniBroj

	SET @res = 1

	RETURN 
END
GO
/****** Object:  StoredProcedure [dbo].[spProsecanProfitKurir]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROC [dbo].[spProsecanProfitKurir]
@brIsporuka int,
@res decimal(10,3) output
AS
BEGIN

	IF @brIsporuka IS NULL OR @brIsporuka = 0
	BEGIN
		SET @res = 0
		RETURN
	END

	IF @brIsporuka = -1
	BEGIN
		SELECT @res = CAST(AVG(Profit) AS decimal(10,3))
		FROM Kurir
		RETURN
	END

	SELECT @res = CAST(AVG(Profit) AS decimal(10,3))
	FROM Kurir
	WHERE BrojIsprucenihPaketa = @brIsporuka

	RETURN 
END
GO
/****** Object:  StoredProcedure [dbo].[spTrenutnaLokacijaPaket]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO



CREATE PROC [dbo].[spTrenutnaLokacijaPaket]
@id int,
@ret int output
AS
BEGIN

	IF @id IS NULL
	BEGIN
		SET @ret = -1
		RETURN
	END
	
	IF NOT EXISTS (SELECT * FROM [Paket] WHERE ID = @id)
	BEGIN
		SET @ret = -1
		RETURN
	END

	IF (SELECT UVozilu FROM [Paket] WHERE ID = @id) = 1
	BEGIN
		SET @ret = -1
		RETURN
	END

	SELECT @ret = G.ID
	FROM [Grad] G, [Adresa] A, [Paket] P
	WHERE P.ID = @id AND P.TrenutnaLokacija = A.ID AND A.Grad = G.ID
	
	RETURN

END
GO
/****** Object:  StoredProcedure [dbo].[spVratiVozilo]    Script Date: 16-Jul-20 1:46:58 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE [dbo].[spVratiVozilo]
	@mag int,
	@reg varchar(100),
	@user varchar(100)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	IF @mag IS NULL OR @reg IS NULL
	BEGIN
		RETURN
	END

	INSERT INTO [ParkiranoVozilo] (Magacin, RegistracioniBroj) VALUES (@mag, @reg)

	UPDATE [Kurir] SET Vozi = NULL, Status = 0 WHERE KorisnickoIme = @user

	RETURN

END
GO
