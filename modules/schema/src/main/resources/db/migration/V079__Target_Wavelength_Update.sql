
--
-- Updates target table radial velocity from int
-- to (signed) numeric milliarcseconds.
--

ALTER TABLE target
  ALTER COLUMN rv     SET DATA TYPE numeric(12,3);

COMMENT ON COLUMN target.rv IS '(sidereal) radial velocity in m/sec, positive if receding';

--
-- Add Z band
--
COPY e_magnitude_band (id, short_name, long_name, center, width, default_system) FROM stdin;
Z	Z	Z	876	95	Vega
\.
