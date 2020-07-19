SET IDENTITY_INSERT [dbo].[Grad] ON 

INSERT [dbo].[Grad] ([ID], [Naziv], [PostanskiBroj]) VALUES (70, N'Belgrade', N'11000')
INSERT [dbo].[Grad] ([ID], [Naziv], [PostanskiBroj]) VALUES (71, N'Kragujevac', N'550000')
INSERT [dbo].[Grad] ([ID], [Naziv], [PostanskiBroj]) VALUES (72, N'Valjevo', N'14000')
INSERT [dbo].[Grad] ([ID], [Naziv], [PostanskiBroj]) VALUES (73, N'Cacak', N'32000')
SET IDENTITY_INSERT [dbo].[Grad] OFF
SET IDENTITY_INSERT [dbo].[Adresa] ON 

INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (87, N'Kraljice Natalije', N'37', 70, CAST(11.000 AS Decimal(10, 3)), CAST(15.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (88, N'Bulevar kralja Aleksandra', N'73', 70, CAST(10.000 AS Decimal(10, 3)), CAST(10.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (89, N'Vojvode Stepe', N'39', 70, CAST(1.000 AS Decimal(10, 3)), CAST(-1.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (90, N'Takovska', N'7', 70, CAST(11.000 AS Decimal(10, 3)), CAST(12.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (91, N'Bulevar kralja Aleksandra', N'37', 70, CAST(12.000 AS Decimal(10, 3)), CAST(12.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (92, N'Daniciceva', N'1', 71, CAST(4.000 AS Decimal(10, 3)), CAST(310.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (93, N'Dure Pucara Starog', N'2', 71, CAST(11.000 AS Decimal(10, 3)), CAST(320.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (94, N'Cika Ljubina', N'8', 72, CAST(102.000 AS Decimal(10, 3)), CAST(101.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (95, N'Karadjordjeva', N'122', 72, CAST(104.000 AS Decimal(10, 3)), CAST(103.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (96, N'Milovana Glisica', N'45', 72, CAST(101.000 AS Decimal(10, 3)), CAST(101.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (97, N'Zupana Stracimira', N'1', 73, CAST(110.000 AS Decimal(10, 3)), CAST(309.000 AS Decimal(10, 3)))
INSERT [dbo].[Adresa] ([ID], [Ulica], [Broj], [Grad], [X], [Y]) VALUES (98, N'Bulevar Vuka Karadzica', N'1', 73, CAST(111.000 AS Decimal(10, 3)), CAST(315.000 AS Decimal(10, 3)))
SET IDENTITY_INSERT [dbo].[Adresa] OFF
INSERT [dbo].[Korisnik] ([Ime], [Prezime], [Sifra], [Adresa], [KorisnickoIme]) VALUES (N'Svetislav', N'Kisprdilov', N'Test_123', 87, N'crno.dete')
INSERT [dbo].[Korisnik] ([Ime], [Prezime], [Sifra], [Adresa], [KorisnickoIme]) VALUES (N'Pera', N'Peric', N'Postar_73', 88, N'postarBG')
INSERT [dbo].[Korisnik] ([Ime], [Prezime], [Sifra], [Adresa], [KorisnickoIme]) VALUES (N'Pera', N'Peric', N'Postar_73', 88, N'postarVA')
INSERT [dbo].[LokacijaMagacina] ([Adresa]) VALUES (87)
INSERT [dbo].[LokacijaMagacina] ([Adresa]) VALUES (94)
SET IDENTITY_INSERT [dbo].[Paket] ON 

INSERT [dbo].[Paket] ([ID], [Status], [VremeKreiranja], [VremePrihvatanja], [TrenutnaLokacija], [Odrediste], [Polaziste], [Korisnik], [Tip], [Tezina], [Cena], [UVozilu], [UMagacinu]) VALUES (31, 3, CAST(N'2020-07-16T18:45:51.490' AS DateTime), CAST(N'2020-07-16T18:45:51.513' AS DateTime), 97, 97, 88, N'crno.dete', 0, CAST(2.000 AS Decimal(10, 3)), CAST(115.000 AS Decimal(10, 3)), 0, 0)
INSERT [dbo].[Paket] ([ID], [Status], [VremeKreiranja], [VremePrihvatanja], [TrenutnaLokacija], [Odrediste], [Polaziste], [Korisnik], [Tip], [Tezina], [Cena], [UVozilu], [UMagacinu]) VALUES (32, 3, CAST(N'2020-07-16T18:45:51.537' AS DateTime), CAST(N'2020-07-16T18:45:51.540' AS DateTime), 94, 94, 89, N'crno.dete', 1, CAST(4.000 AS Decimal(10, 3)), CAST(175.000 AS Decimal(10, 3)), 0, 0)
INSERT [dbo].[Paket] ([ID], [Status], [VremeKreiranja], [VremePrihvatanja], [TrenutnaLokacija], [Odrediste], [Polaziste], [Korisnik], [Tip], [Tezina], [Cena], [UVozilu], [UMagacinu]) VALUES (33, 3, CAST(N'2020-07-16T18:45:51.543' AS DateTime), CAST(N'2020-07-16T18:45:51.543' AS DateTime), 92, 92, 90, N'crno.dete', 2, CAST(5.000 AS Decimal(10, 3)), CAST(250.000 AS Decimal(10, 3)), 0, 0)
INSERT [dbo].[Paket] ([ID], [Status], [VremeKreiranja], [VremePrihvatanja], [TrenutnaLokacija], [Odrediste], [Polaziste], [Korisnik], [Tip], [Tezina], [Cena], [UVozilu], [UMagacinu]) VALUES (34, 1, CAST(N'2020-07-16T18:45:51.663' AS DateTime), CAST(N'2020-07-16T18:45:51.663' AS DateTime), 88, 93, 88, N'crno.dete', 3, CAST(2.000 AS Decimal(10, 3)), CAST(350.000 AS Decimal(10, 3)), 0, 0)
SET IDENTITY_INSERT [dbo].[Paket] OFF
INSERT [dbo].[Vozilo] ([RegistracioniBroj], [TipGoriva], [Potrosnja], [Nosivost]) VALUES (N'BG1675DA', 2, CAST(6.300 AS Decimal(10, 3)), CAST(1000.500 AS Decimal(10, 3)))
INSERT [dbo].[Vozilo] ([RegistracioniBroj], [TipGoriva], [Potrosnja], [Nosivost]) VALUES (N'VA1675DA', 1, CAST(7.300 AS Decimal(10, 3)), CAST(500.500 AS Decimal(10, 3)))
INSERT [dbo].[Kurir] ([KorisnickoIme], [Vozi], [BrojIsprucenihPaketa], [Profit], [Status], [BrojDozvole]) VALUES (N'postarBG', NULL, 3, CAST(167212.006 AS Decimal(10, 3)), 0, N'654321')
INSERT [dbo].[Kurir] ([KorisnickoIme], [Vozi], [BrojIsprucenihPaketa], [Profit], [Status], [BrojDozvole]) VALUES (N'postarVA', NULL, 0, CAST(0.000 AS Decimal(10, 3)), 0, N'123456')
INSERT [dbo].[KoristioVozilo] ([KorisnickoIme], [RegistracioniBroj]) VALUES (N'postarBG', N'BG1675DA')
INSERT [dbo].[ParkiranoVozilo] ([Magacin], [RegistracioniBroj]) VALUES (87, N'BG1675DA')
INSERT [dbo].[ParkiranoVozilo] ([Magacin], [RegistracioniBroj]) VALUES (94, N'VA1675DA')