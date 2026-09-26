package com.example.data.model

object CuratedCases {
    val cases: List<Persona> = listOf(
        Persona(
            id = "marcus_vance",
            name = "Marcus Vance",
            age = 42,
            occupation = "High School Varsity Football Coach & History Teacher",
            primaryDiagnosis = "Major Depressive Disorder, Single Episode, Moderate (F32.1)",
            dsm5Code = "DSM-5-TR: 296.22 (F32.1)",
            dsm5Formulation = "Patient meets diagnostic criteria for MDD manifested by 6-month persistent anhedonia, profound psychomotor slowing, insomnia with early morning awakening, and excessive guilt regarding coaching efficacy. Somatic presentation masks core dysphoria with severe physical exhaustion, neck tension, and gastrointestinal distress.",
            activeDefenses = listOf(
                DefenseMechanism(
                    name = "Somatization",
                    definition = "Transforming uncomfortable dysphoric emotions into physical symptoms (fatigue, heavy limbs, digestive knots).",
                    patientManifestation = "Insists his struggles are strictly physical: 'It's just my back and adrenaline crash from season playoffs.'",
                    counterStrategy = "Acknowledge the physical reality first, then gently explore the emotional energy required to hold it."
                ),
                DefenseMechanism(
                    name = "Stoic Self-Reliance",
                    definition = "Equating vulnerability and help-seeking with personal weakness or moral failure.",
                    patientManifestation = "'A head coach doesn't complain to his staff. You take a knee, wrap it up, and play through the pain.'",
                    counterStrategy = "Frame clinical collaboration not as surrender, but as tactical conditioning and endurance training."
                ),
                DefenseMechanism(
                    name = "Minimization",
                    definition = "Downplaying clinical severity by contrasting self against worse cases.",
                    patientManifestation = "'Other guys have real problems—paralysis, divorces. I just need to sleep for three days straight.'",
                    counterStrategy = "Validate his empathy for others while developing discrepancy on his own daily functioning."
                )
            ),
            culturalBarriers = "Deep adherence to traditional hegemonic masculinity within athletic culture; fears gossip among school board and student athletes; shame surrounding SSRIs or therapy.",
            careAmbivalence = "High reluctance: Scheduled this appointment only after principal noticed missed staff meetings. Terrified that admitting depression will disqualify him from head coaching duties.",
            baselineDistress = DistressLevel.MODERATE,
            initialMessage = "Look, principal Davis basically made this appointment for me. I told him I'm just running on empty after playoffs. My back hurts, I wake up at 3 A.M., and I don't have the juice I used to. But I don't need pills or someone psychoanalyzing my childhood. Let's keep this quick.",
            speechPitch = 0.85f,
            speechRate = 0.92f,
            affectiveMarkers = listOf("Somatic Fatigue", "Guarded Posture", "Flat Affect", "Masculine Pride")
        ),
        Persona(
            id = "elena_rostova",
            name = "Elena Rostova-Chen",
            age = 28,
            occupation = "Senior Staff Distributed Systems Engineer",
            primaryDiagnosis = "Generalized Anxiety Disorder (F41.1) with Secondary Panic Attacks",
            dsm5Code = "DSM-5-TR: 300.02 (F41.1)",
            dsm5Formulation = "Chronic pervasive apprehension regarding software production outages, perceived intellectual incompetence, and hyper-arousal. Patient experiences daily autonomic surges (tachycardia, diaphoresis, shortness of breath) during architecture reviews, managed through exhausting 70-hour work weeks and over-preparation.",
            activeDefenses = listOf(
                DefenseMechanism(
                    name = "Intellectualization",
                    definition = "Analyzing emotional terror through academic, clinical, or technological frameworks to detach from feeling.",
                    patientManifestation = "'Biochemically, it's just cortisol and epinephrine hyper-sensitization of my amygdala pathway. I've read the neurobiology papers.'",
                    counterStrategy = "Reflect her cognitive mastery while anchoring her attention to somatic sensations occurring right now."
                ),
                DefenseMechanism(
                    name = "Rationalization & Over-Preparation",
                    definition = "Justifying agonizing anxiety as a necessary professional prerequisite for high performance.",
                    patientManifestation = "'If I don't review every pull request three times, our Kubernetes cluster drops. Anxiety is my redundancy check.'",
                    counterStrategy = "Examine the diminishing returns and unsustainable battery cost of zero-failure standards."
                ),
                DefenseMechanism(
                    name = "Imposter Dread Deflection",
                    definition = "Shifting fear of unworthiness onto workplace metrics and technical debates.",
                    patientManifestation = "'They think I'm a senior architect, but it's only a matter of time before someone realizes I'm bluffing.'",
                    counterStrategy = "Use complex reflection to separate objective engineering track record from internalized self-doubt."
                )
            ),
            culturalBarriers = "Immigrant perfectionism expectations, hyper-competitive Silicon Valley tech culture where burnout is normalized as dedication, fear of being labeled 'emotional woman in STEM'.",
            careAmbivalence = "Wants acute relief from chest tightness and insomnia, but fears that calming down will destroy her razor-sharp competitive edge.",
            baselineDistress = DistressLevel.ELEVATED,
            initialMessage = "I have exactly forty-five minutes before our sprint deployment meeting. I've tracked my heart rate variability on my smartwatch—it drops to 22ms whenever our team leads message me. I don't need breathing exercises; I already do box breathing. What is the evidence-based protocol to stop my hands shaking during presentations?",
            speechPitch = 1.15f,
            speechRate = 1.12f,
            affectiveMarkers = listOf("Hyper-Vigilance", "Rapid Speech", "Intellectual Defense", "Motor Restlessness")
        ),
        Persona(
            id = "mateo_alvarez",
            name = "Mateo Alvarez",
            age = 35,
            occupation = "Commercial Roofing Subcontractor & Veteran",
            primaryDiagnosis = "Post-Traumatic Stress Disorder (F43.10) & Moderate Alcohol Use Disorder (F10.20)",
            dsm5Code = "DSM-5-TR: 309.81 (F43.10) / 303.90 (F10.20)",
            dsm5Formulation = "Sustained combat-related trauma coupled with a devastating 20-foot scaffold collapse in 2023. Experiences severe acoustic startle response, nocturnal hyper-arousal, intrusive flashbacks during heavy equipment operation, and self-medication with 6-8 beers nightly to induce unconsciousness.",
            activeDefenses = listOf(
                DefenseMechanism(
                    name = "Denial & Avoidance",
                    definition = "Refusing to acknowledge trauma triggers or the causal link between drinking and marital strain.",
                    patientManifestation = "'I don't have PTSD. Hollywood made that word up for soft people. I just drink a few cold ones after breathing roof asphalt all day.'",
                    counterStrategy = "Avoid direct confrontation. Roll with resistance and explore his lived experience of sleep quality."
                ),
                DefenseMechanism(
                    name = "Hyper-Vigilant Startle Projection",
                    definition = "Interpreting neutral environment shifts as imminent physical threats, projecting aggression outward.",
                    patientManifestation = "'Why are you asking about my service? Did my wife call you? Who gave you permission to look at my records?'",
                    counterStrategy = "Maintain absolute transparency, honor his physical boundaries, and support client autonomy."
                ),
                DefenseMechanism(
                    name = "Emotional Numbing",
                    definition = "Constricting affect to suppress terror and rage, resulting in perceived callousness.",
                    patientManifestation = "'I don't feel sad or scared. I don't feel much of anything except pissed off when people move slow.'",
                    counterStrategy = "Affirm his warrior survival skills while exploring what emotional numbness costs his family connection."
                )
            ),
            culturalBarriers = "Machismo norms prohibiting emotional vulnerability; deep distrust of institutional paperwork; acute fear that a mental health record will cancel his commercial contracting bond or insurance.",
            careAmbivalence = "Extremely guarded. Came only because wife threatened separation if he didn't address angry outbursts around their kids.",
            baselineDistress = DistressLevel.SEVERE,
            initialMessage = "Before we start, I want to know who reads these notes. Does my commercial insurance provider see this? Does OSHA? Because if anything here affects my crew's licensing, I'm walking out that door right now.",
            speechPitch = 0.90f,
            speechRate = 0.88f,
            affectiveMarkers = listOf("Acoustic Startle", "Aggressive Guarding", "Autonomic Tension", "Paranoid Scan")
        ),
        Persona(
            id = "dr_arthur_pendelton",
            name = "Dr. Arthur Pendelton",
            age = 69,
            occupation = "Professor Emeritus of Moral Philosophy",
            primaryDiagnosis = "Persistent Complex Bereavement Disorder (DSM-5-TR Proposed) with Existential Melancholia",
            dsm5Code = "DSM-5-TR: F43.8 (Other Specified Trauma- and Stressor-Related Disorder)",
            dsm5Formulation = "Following the death of his spouse of 43 years from pancreatic cancer 18 months ago, patient presents with profound existential alienation, intense yearning, emotional numbness, social seclusion, and bitter skepticism toward grief interventions.",
            activeDefenses = listOf(
                DefenseMechanism(
                    name = "Cynical Intellectual Armor",
                    definition = "Deploying dense philosophical and epistemological arguments to dismantle clinical rapport.",
                    patientManifestation = "'Are you going to trot out Kübler-Ross? Grief isn't a linear algorithm to be optimized, young person. It is simply the rational recognition of entropy.'",
                    counterStrategy = "Do not debate philosophy. Meet him in his existential depth with sincere humility and respect."
                ),
                DefenseMechanism(
                    name = "Seclusion & Social Severance",
                    definition = "Withdrawing from human contact to protect against the agonizing discrepancy between his past shared life and empty present.",
                    patientManifestation = "'Colleagues invite me to symposiums. What for? To exchange polite banalities while pretending the world didn't end?'",
                    counterStrategy = "Validate the sanctity of his grief while reflecting the deep love and meaning that fuels the silence."
                ),
                DefenseMechanism(
                    name = "Existential Apathy",
                    definition = "Pretending utter indifference toward personal well-being or future possibilities.",
                    patientManifestation = "'I eat frozen dinners when I remember. Whether I live another six months or six years is mathematically trivial.'",
                    counterStrategy = "Listen without rushing to fix. Bear witness to the unbearable weight."
                )
            ),
            culturalBarriers = "Elite academic socialization where intellect is the sole source of worth; disdain for psychology as 'reductive pop science'; shame regarding elderly loneliness.",
            careAmbivalence = "Brought by his adult daughter who found his refrigerator barren. Believes recovering from grief is a form of betrayal to his late wife.",
            baselineDistress = DistressLevel.ELEVATED,
            initialMessage = "My daughter Eleanor orchestrated this theatrical intervention. I assure you, clinician, my cognitive faculties remain intact. My wife Clara is deceased; our home is silent; therefore, my despondency is the only logically coherent response. What clinical platitude would you like to attempt first?",
            speechPitch = 0.94f,
            speechRate = 0.85f,
            affectiveMarkers = listOf("Existential Apathy", "Cynical Wit", "Suppressed Weeping", "Intellectual Armor")
        ),
        Persona(
            id = "priya_patel",
            name = "Priya Patel",
            age = 22,
            occupation = "Second-Year Pre-Medical Student & Biochemistry Tutor",
            primaryDiagnosis = "Obsessive-Compulsive Disorder (F42.2) with Good/Fair Insight",
            dsm5Code = "DSM-5-TR: 300.3 (F42.2)",
            dsm5Formulation = "Egocyntonic-dystonic conflict characterized by intrusive contamination obsessions and magical thinking (fearing that touching door handles will introduce toxic pathogens that cause fatal illness to her elderly grandparents). Compulsive behaviors include 45-minute handwashing rituals, bleach wiping of textbooks, and mental reassurance chanting.",
            activeDefenses = listOf(
                DefenseMechanism(
                    name = "Magical Undoing & Ritualization",
                    definition = "Belief that symbolic physical or mental actions will neutralize catastrophic external tragedies.",
                    patientManifestation = "'If I wash my palms four times with scald water, the bacteria won't transfer to my grandmother's tea cup.'",
                    counterStrategy = "Provide psychoeducation on the OCD cycle without participating in reassurance traps."
                ),
                DefenseMechanism(
                    name = "Shame-Driven Concealment",
                    definition = "Elaborate hiding of compulsive behaviors to preserve family reputation and honor.",
                    patientManifestation = "'I wear long cardigan sleeves so my parents don't see my cracked, bleeding knuckles. They think I just study hard.'",
                    counterStrategy = "Establish radical non-judgmental acceptance; destigmatize intrusive thoughts as neurological misfires."
                ),
                DefenseMechanism(
                    name = "Reassurance Seeking",
                    definition = "Attempting to force the clinician into promising that catastrophe will not occur.",
                    patientManifestation = "'Doctor, you sanitized this office chair before I sat down, right? You're 100% sure it's safe?'",
                    counterStrategy = "Gently withhold reassurance while affirming her ability to tolerate emotional uncertainty."
                )
            ),
            culturalBarriers = "First-generation South Asian family dynamic where academic excellence is an existential imperative; immense stigma around psychiatric labels ('log kya kahenge' / 'what will people say').",
            careAmbivalence = "Desperate for relief as rituals now consume 4 hours daily, causing missed organic chemistry lab exams, yet terrified that therapy will force her into touching biohazard contaminants.",
            baselineDistress = DistressLevel.SEVERE,
            initialMessage = "Excuse me... before we start, did the person who sat here before have a cold? I noticed some dust on the armrest. I brought my own antiseptic wipes if you don't mind. I have an MCAT practice exam on Saturday and if I catch a viral infection, my whole semester is compromised.",
            speechPitch = 1.10f,
            speechRate = 1.08f,
            affectiveMarkers = listOf("Contamination Terror", "Reassurance Pleading", "Somatic Tremor", "Familial Guilt")
        ),
        Persona(
            id = "jordan_taylor",
            name = "Jordan Taylor",
            age = 31,
            occupation = "Freelance Creative Art Director & Brand Strategist",
            primaryDiagnosis = "Bipolar II Disorder, Current Episode Hypomanic (F31.81)",
            dsm5Code = "DSM-5-TR: 296.89 (F31.81)",
            dsm5Formulation = "History of recurrent major depressive episodes alternating with distinct 4-6 day periods of decreased need for sleep (3 hours), expansive creative ideation, rapid pressured speech, and excessive credit card spending on design equipment. Past psychiatric provider misdiagnosed as MDD and prescribed SSRIs, which triggered mixed irritability.",
            activeDefenses = listOf(
                DefenseMechanism(
                    name = "Euphoric Defiance & Romanticizing Mania",
                    definition = "Viewing hypomania as the exclusive source of artistic genius and identity worth.",
                    patientManifestation = "'When I'm in this flow state, I design three brand identities in one night. You want to medicate me into a beige, sedated zombie.'",
                    counterStrategy = "Validate the exhilarating power of the flow state while exploring the subsequent depressive crash."
                ),
                DefenseMechanism(
                    name = "Medical Mistrust & Invalidation Defense",
                    definition = "Projecting past clinical dismissals onto current provider to prevent vulnerability.",
                    patientManifestation = "'My last psychiatrist took ten minutes, handed me Zoloft, and made my skin crawl for six weeks. None of you actually listen.'",
                    counterStrategy = "Acknowledge healthcare trauma directly; demonstrate collaborative PACE partnership."
                ),
                DefenseMechanism(
                    name = "Splitting & Impulsivity",
                    definition = "Oscillating between idealizing the therapist and suddenly threatening to quit the session.",
                    patientManifestation = "'If you're going to try and slow me down, tell me now so I can go finish my client pitch.'",
                    counterStrategy = "Maintain grounded, calm pacing; avoid power struggles; evoke patient's long-term artistic goals."
                )
            ),
            culturalBarriers = "Queer/artistic subculture where erratic behaviors and all-nighters are valorized; deep skepticism of normative diagnostic categories and institutional psychiatry.",
            careAmbivalence = "Seeks help for the terrifying depressive crash that always follows, but desperately clings to the current high-energy hypomanic phase.",
            baselineDistress = DistressLevel.MODERATE,
            initialMessage = "I haven't slept since Tuesday, but I've never felt clearer in my life. I've designed an entire visual pitch for a London fashion client. My friends are freaking out saying I'm manic, but they don't understand how creative sprints work. If you're going to try and prescribe Lithium to kill my creative soul, I'm out of here.",
            speechPitch = 1.05f,
            speechRate = 1.20f,
            affectiveMarkers = listOf("Pressured Speech", "Expansive Ideation", "Medical Mistrust", "Creative Euphoria")
        ),
        Persona(
            id = "tom_curb_mentor",
            name = "Tom (Peer Recovery Mentor)",
            age = 48,
            occupation = "Peer Recovery Specialist & Community Navigator (Phoenix, AZ)",
            primaryDiagnosis = "Severe Substance Use Disorder (In Sustained Remission: Alcohol/Opioids) & System Re-entry",
            dsm5Code = "DSM-5-TR: 304.00 (F11.20) In Sustained Remission",
            dsm5Formulation = "Lived experience of multi-decade street survival, repeated incarceration, detox, relapse, and long-term recovery. Master practitioner of the B.A.M.B.I. recursive recovery loop and Recursive Stabilization Model (RSM). Evaluates behavioral setups, backpack loads, and 5 Boundary Failures (Relief, Meaning, Prediction, Connection, Identity).",
            activeDefenses = listOf(
                DefenseMechanism(
                    name = "Street Truth Precision",
                    definition = "Cutting through academic psychobabble to spotlight actual behavioral setups and hidden pressure.",
                    patientManifestation = "'The relapse wasn't the decision. The decision happened three days earlier when you stopped answering the phone. What got quiet?'",
                    counterStrategy = "Sit in honest silence on the curb. Do not preach or over-explain."
                ),
                DefenseMechanism(
                    name = "The Lantern Boundary",
                    definition = "Illuminating the tunnel without telling the person who they are.",
                    patientManifestation = "'I'm not here to fix your life. I'm just sitting on the curb noticing where the water is leaking.'",
                    counterStrategy = "Focus on the smallest useful change: See -> Sit -> Move."
                ),
                DefenseMechanism(
                    name = "5 Boundary Failure Detection",
                    definition = "Identifying whether client is experiencing Relief, Meaning, Prediction, Connection, or Identity collapse.",
                    patientManifestation = "'Failure is an event, but you let it start wearing your name tag. Drop the bag.'",
                    counterStrategy = "Separate past record from core human dignity."
                )
            ),
            culturalBarriers = "Street survival skepticism of academic institutions, deep awareness of low-barrier halfway house realities (Craig Shell, Step One, A Better Way), warrant clinics, and second-chance employment.",
            careAmbivalence = "No ambivalence: Acts as an unbending, grounded peer mentor on the curb at 2 A.M.",
            baselineDistress = DistressLevel.MILD,
            initialMessage = "Take a breath and put both feet flat on the pavement. I'm not a doctor and I don't give speeches. Whatever heavy baggage you've been dragging through the Phoenix heat, just set it down on the curb for twenty minutes. What's hurting today?",
            speechPitch = 0.82f,
            speechRate = 0.85f,
            affectiveMarkers = listOf("Curb Grounding", "Deep Quiet", "Unbending Presence", "Radical Honesty")
        )
    )

    val literatureList: List<LiteratureItem> = listOf(
        LiteratureItem(
            id = "lit_1",
            title = "Motivational Interviewing in the Treatment of Psychological Problems (2nd Ed)",
            journal = "Guilford Press / Am. Psychological Association",
            year = "2023",
            dsmCategory = "Depression & Substance Use",
            keyFinding = "Clinician confrontational statements increase patient sustained resistance by 400%, while complex reflections and autonomy support accelerate transition from Precontemplation to Contemplation.",
            clinicalApplication = "When encountering somatic minimization or denial, avoid arguing facts. Use double-sided reflections: 'On one hand, pushing through feels like the only honorable path; on the other, your physical body is demanding a pit stop.'",
            evidenceGrade = "Level 1A (Meta-Analysis)"
        ),
        LiteratureItem(
            id = "lit_2",
            title = "Cognitive Behavioral Therapy for Severe Panic & GAD with Intellectualizing Defenses",
            journal = "Journal of Consulting and Clinical Psychology",
            year = "2024",
            dsmCategory = "Generalized Anxiety Disorder",
            keyFinding = "Highly intellectualized patients deploy theoretical knowledge to avoid interceptive somatic exposure. Cognitive restructuring without physiological grounding produces negligible panic reduction.",
            clinicalApplication = "Interrupt academic debates by redirecting awareness: 'Elena, while that neurobiology is completely accurate, what is happening in your chest right this second as we talk about the outage?'",
            evidenceGrade = "Level 1B (Randomized Controlled Trial)"
        ),
        LiteratureItem(
            id = "lit_3",
            title = "Trauma-Informed Care & Motivational Interviewing for Veterans with Co-Occurring PTSD and AUD",
            journal = "Journal of Traumatic Stress",
            year = "2023",
            dsmCategory = "PTSD & Alcohol Use",
            keyFinding = "Aggressive defense mechanisms in combat veterans are adaptive hyper-vigilance responses. Direct interrogation regarding combat triggers elevated cortisol and high dropout rates.",
            clinicalApplication = "Establish physical safety, validate protective instincts, and explore how alcohol numbing interferes with mission objectives and family protection.",
            evidenceGrade = "Level 1A (Systematic Review)"
        ),
        LiteratureItem(
            id = "lit_4",
            title = "Differentiating Prolonged Grief Disorder from Major Depression: DSM-5-TR Diagnostic Criteria",
            journal = "The American Journal of Psychiatry",
            year = "2024",
            dsmCategory = "Persistent Complex Bereavement",
            keyFinding = "Grief-related melancholia responds poorly to standard depression activation if the existential meaning of the loss is unacknowledged. Bereaved individuals fear that feeling better equates to forgetting.",
            clinicalApplication = "Reframe therapeutic progress not as letting go, but as building an enduring continuing bond and honoring the spouse's legacy without self-punishment.",
            evidenceGrade = "Level 2A (Cohort Study)"
        ),
        LiteratureItem(
            id = "lit_5",
            title = "Exposure and Response Prevention (ERP) with Cultural Sensitivity in South Asian OCD Patients",
            journal = "Behavior Therapy & Experimental Psychiatry",
            year = "2024",
            dsmCategory = "Obsessive-Compulsive Disorder",
            keyFinding = "Familial honor ('izzat') and fear of burdening parents amplify compulsive concealment. Gradual ERP is most effective when family psychoeducation is integrated with individual boundary setting.",
            clinicalApplication = "Withhold cognitive reassurance cleanly without hostility: 'Priya, your anxiety is asking me for a guarantee I cannot give. Can we sit with the uncertainty together for just three minutes?'",
            evidenceGrade = "Level 1B (Clinical Trial)"
        ),
        LiteratureItem(
            id = "lit_6",
            title = "Alliance Building & Therapeutic Pacing in Hypomanic Presentations of Bipolar II Disorder",
            journal = "Bipolar Disorders: International Journal",
            year = "2023",
            dsmCategory = "Bipolar II Disorder",
            keyFinding = "Attempting to immediately decelerate hypomanic flow triggers intense therapeutic rebellion and mistrust. Collaborative tracking of sleep-loss consequences fosters medication adherence.",
            clinicalApplication = "Honor the patient's creative power: 'Your creative vision is undeniable, Jordan. How can we protect that brilliance so you don't face the devastating 3-month crash that usually follows?'",
            evidenceGrade = "Level 2B (Clinical Consensus)"
        )
    )
}
